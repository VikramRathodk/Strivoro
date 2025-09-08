package com.devvikram.striveo.ui.screens.home


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devvikram.striveo.config.enums.TaskFilter
import com.devvikram.striveo.config.enums.TaskStatus
import com.devvikram.striveo.room.model.RoomTask
import com.devvikram.striveo.ui.QuickAction
import com.devvikram.striveo.ui.TaskStats
import com.devvikram.striveo.ui.reuseables.common.TaskItemCard

@Composable
fun ProgressSection(stats: TaskStats) {
    val completionRate = if (stats.totalTasks > 0) {
        stats.completedTasks.toFloat() / stats.totalTasks.toFloat()
    } else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = completionRate,
        animationSpec = androidx.compose.animation.core.tween(1000)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 0.5.dp,
            color = MaterialTheme.colorScheme.outline.copy(
                alpha = 0.2f
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Today's Progress",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${stats.completedTasks} of ${stats.totalTasks} tasks completed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                CircularProgressRing(
                    progress = animatedProgress,
                    completedTasks = stats.completedTasks,
                    totalTasks = stats.totalTasks
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                item {
                    StatCard(
                        icon = Icons.Default.CheckCircle,
                        label = "Completed",
                        value = stats.completedTasks.toString(),
                        color = Color(0xFF10B981)
                    )
                }
                item {
                    StatCard(
                        icon = Icons.Default.HourglassEmpty,
                        label = "Pending",
                        value = stats.pendingTasks.toString(),
                        color = Color(0xFFF59E0B)
                    )
                }
                item {
                    StatCard(
                        icon = Icons.Default.Warning,
                        label = "Overdue",
                        value = stats.overdueTasks.toString(),
                        color = Color(0xFFEF4444)
                    )
                }
                item {
                    StatCard(
                        icon = Icons.Default.CenterFocusStrong,
                        label = "Focus",
                        value = stats.focusTime,
                        color = Color(0xFF6366F1)
                    )
                }

            }
        }
    }
}

@Preview
@Composable
fun ProgressSectionPreview() {
    val stats = TaskStats(
        totalTasks = 10,
        completedTasks = 5,
        pendingTasks = 3,
        overdueTasks = 2,
        focusTime = "2h 30m",
        productivity = 75,
        weeklyCompletion = 60
    )
    ProgressSection(stats = stats)
}

@Preview
@Composable
fun CircularProgressRingPreview() {
    CircularProgressRing(progress = 0.75f, completedTasks = 75, totalTasks = 100)
}

@Composable
private fun CircularProgressRing(
    progress: Float,
    completedTasks: Int,
    totalTasks: Int
) {
    val strokeWidth = with(LocalDensity.current) { 8.dp.toPx() }
    val size = 100.dp

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            // Background ring
            drawArc(
                color = Color(0xFF10B981).copy(alpha = 0.2f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth)
            )
            // Progress ring
            drawArc(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF10B981), Color(0xFF34D399)),
                    start = Offset(0f, size.toPx()),
                    end = Offset(size.toPx(), 0f)
                ),
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = strokeWidth)
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$completedTasks/$totalTasks",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF10B981)
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                fontSize = 12.sp,
                color = Color(0xFF10B981).copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 0.5.dp,
            color = MaterialTheme.colorScheme.outline.copy(
                alpha = 0.2f
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color.copy(alpha = 0.15f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = color
                )
            }
        }
    }
}

@Preview
@Composable
fun StatCardPreview() {
    StatCard(
        icon = Icons.Default.CheckCircle,
        label = "Completed",
        value = "50",
        color = Color(0xFF10B981)
    )
}


@Composable
fun TaskFilterSection(selectedFilter: TaskFilter, onFilterSelected: (TaskFilter) -> Unit) {
    Column {
        Text(
            text = "Your Tasks",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(TaskFilter.values()) { filter ->
                FilterChip(
                    onClick = { onFilterSelected(filter) },
                    label = {
                        Text(
                            text = when (filter) {
                                TaskFilter.ALL -> "All"
                                TaskFilter.TODAY -> "Today"
                                TaskFilter.UPCOMING -> "Upcoming"
                                TaskFilter.COMPLETED -> "Completed"
                                TaskFilter.OVERDUE -> "Overdue"
                            },
                            fontSize = 14.sp
                        )
                    },
                    selected = selectedFilter == filter,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF6366F1),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
    }
}

@Preview
@Composable
fun TaskFilterSectionPreview() {
    TaskFilterSection(selectedFilter = TaskFilter.ALL, onFilterSelected = {})
}

@Composable
fun StreakSection(
    currentStreak: Int,
    longestStreak: Int,
    weeklyGoal: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔥 Your Streak",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (currentStreak > 0) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFF6B35).copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Active",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFFF6B35)
                        )
                    }
                }
            }

            // Main streak display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFFF6B35).copy(alpha = 0.1f),
                                Color(0xFFF7931E).copy(alpha = 0.1f)
                            )
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = currentStreak.toString(),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6B35)
                    )
                    Text(
                        text = if (currentStreak == 1) "day streak" else "days streak",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    if (currentStreak == 0) {
                        Text(
                            text = "Complete a task to start your streak!",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Longest streak
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = longestStreak.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Best Streak",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                // Weekly goal
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = weeklyGoal.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981)
                    )
                    Text(
                        text = "Weekly Goal",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                // Streak level indicator
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = getStreakLevel(currentStreak),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Level",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // Motivational message
            if (currentStreak > 0) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = getMotivationalMessage(currentStreak),
                        modifier = Modifier.padding(12.dp),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

private fun getStreakLevel(streak: Int): String {
    return when {
        streak >= 30 -> "🏆 Master"
        streak >= 21 -> "💎 Expert"
        streak >= 14 -> "⭐ Pro"
        streak >= 7 -> "🚀 Rising"
        streak >= 3 -> "🌱 Growing"
        streak > 0 -> "🔥 Starter"
        else -> "💤 Sleepy"
    }
}

private fun getMotivationalMessage(streak: Int): String {
    return when {
        streak >= 30 -> "Incredible! You're a true productivity master! 🎉"
        streak >= 21 -> "Amazing! You've built an unbreakable habit! 💪"
        streak >= 14 -> "Fantastic! Two weeks of consistent progress! 🌟"
        streak >= 7 -> "Great job! One week streak - you're on fire! 🔥"
        streak >= 3 -> "Nice work! Building momentum day by day! 📈"
        else -> "Keep it up! Every day counts! 💫"
    }
}

@Preview
@Composable
fun StreakSectionPreview() {
    StreakSection(
        currentStreak = 5,
        longestStreak = 12,
        weeklyGoal = 7
    )
}

@Composable
fun QuickActionsSection(actions: List<QuickAction>) {
    Column {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(actions) { action ->
                QuickActionCard(action)
            }
        }
    }
}

@Preview
@Composable
fun QuickActionsSectionPreview() {
    val actions = listOf(
        QuickAction(
            icon = Icons.Filled.CheckCircle,
            title = "Add Task",
            subtitle = "Create a new task",
            color = Color.Blue,
            onClick = {}
        )
    )
    QuickActionsSection(actions = actions)
}

@Preview
@Composable
fun QuickActionCardPreview() {
    val action = QuickAction(
        icon = Icons.Filled.CheckCircle,
        title = "Add Task",
        subtitle = "Create a new task",
        color = Color.Blue,
        onClick = {}
    )
    QuickActionCard(action = action)
}

@Composable
fun QuickActionCard(action: QuickAction) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .clickable { action.onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = action.color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = action.title,
                tint = action.color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = action.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = action.color,
                textAlign = TextAlign.Center
            )
            Text(
                text = action.subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}



@Composable
fun TasksSection(
    roomTasks: List<RoomTask>,
    filter: TaskFilter,
    onTaskToggle: (String) -> Unit,
    onTaskClick: (String) -> Unit,
    onStatusChange: (String, TaskStatus) -> Unit,
    viewModel: HomeViewModel,
) {
    Column {
        if (roomTasks.isEmpty()) {
            EmptyTasksState(filter)
        } else {
            roomTasks.forEach { task ->

                TaskItemCard(
                    roomTask = task,
                    onToggle = { onTaskToggle(task.taskId) },
                    onClick = { onTaskClick(task.taskId) },
                    viewModel = viewModel,
                    onStatusChange = {
                        onStatusChange(task.taskId, it)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}


@Composable
fun EmptyTasksState(filter: TaskFilter) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = when (filter) {
                    TaskFilter.TODAY -> "📅"
                    TaskFilter.UPCOMING -> "⏰"
                    TaskFilter.COMPLETED -> "✅"
                    TaskFilter.OVERDUE -> "⚠️"
                    else -> "📝"
                },
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = when (filter) {
                    TaskFilter.TODAY -> "No tasks for today"
                    TaskFilter.UPCOMING -> "No upcoming tasks"
                    TaskFilter.COMPLETED -> "No completed tasks"
                    TaskFilter.OVERDUE -> "No overdue tasks"
                    else -> "No tasks found"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Tap the + button to create your first task",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
fun EmptyTasksStatePreview() {
    EmptyTasksState(filter = TaskFilter.TODAY)
}

@Composable
fun WeeklySummarySection(stats: TaskStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF6366F1).copy(alpha = 0.05f)
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Weekly Summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF6366F1)
                )
                Text(
                    text = "${stats.weeklyCompletion}% completion rate this week",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "🎯 ${stats.productivity}% productivity • ⚡ ${stats.focusTime} focused",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Icon(
                imageVector = Icons.Filled.TrendingUp,
                contentDescription = "Weekly Trend",
                tint = Color(0xFF10B981),
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Preview
@Composable
fun WeeklySummarySectionPreview() {
    val stats = TaskStats(
        totalTasks = 10,
        completedTasks = 5,
        pendingTasks = 3,
        overdueTasks = 2,
        focusTime = "2h 30m",
        productivity = 75,
        weeklyCompletion = 60
    )
    WeeklySummarySection(stats = stats)
}