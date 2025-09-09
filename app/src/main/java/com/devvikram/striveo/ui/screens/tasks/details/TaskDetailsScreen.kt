package com.devvikram.striveo.ui.screens.tasks.details

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devvikram.striveo.config.enums.TaskPriority
import com.devvikram.striveo.config.enums.TaskStatus
import com.devvikram.striveo.room.model.RoomTask

@Composable
fun TaskDetailsScreen(
    viewModel: TaskDetailsViewModel,
    onBackPressed: () -> Unit
) {
    val taskDetailState by viewModel.taskDetailState.collectAsState()
    val taskUpdateState by viewModel.taskUpdateState.collectAsState()

    BackHandler {
        onBackPressed()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top app bar
        item {
            TaskDetailsTopBar(
                onBackClick = onBackPressed
            )
        }

        // Content based on state
        when (taskDetailState) {
            is TaskDetailsViewModel.TaskDetailState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            is TaskDetailsViewModel.TaskDetailState.Success -> {
                // Add all content items directly to the main LazyColumn
                item {
                    this@LazyColumn.TaskDetailsContentItems(
                        task = (taskDetailState as TaskDetailsViewModel.TaskDetailState.Success).task,
                        taskUpdateState = taskUpdateState,
                        onToggleCompletion = {
                            viewModel.toggleTaskCompletion((taskDetailState as TaskDetailsViewModel.TaskDetailState.Success).task.taskId)
                        },
                        onStatusChange = { newStatus ->
                            viewModel.updateTaskStatus(
                                (taskDetailState as TaskDetailsViewModel.TaskDetailState.Success).task.taskId,
                                newStatus
                            )
                        },
                        viewModel = viewModel
                    )
                }
            }

            is TaskDetailsViewModel.TaskDetailState.Error -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = (taskDetailState as TaskDetailsViewModel.TaskDetailState.Error).message,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            is TaskDetailsViewModel.TaskDetailState.Idle -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Loading task details...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LazyListScope.TaskDetailsContentItems(
    task: RoomTask,
    taskUpdateState: TaskDetailsViewModel.TaskUpdateState,
    onToggleCompletion: () -> Unit,
    onStatusChange: (TaskStatus) -> Unit,
    viewModel: TaskDetailsViewModel
) {
    var showStatusDropdown by remember { mutableStateOf(false) }

    item {
        // Main Task Card with all content
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (task.isCompleted)
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                else MaterialTheme.colorScheme.surface
            ),
            border = when (taskUpdateState) {
                is TaskDetailsViewModel.TaskUpdateState.Loading -> BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )

                is TaskDetailsViewModel.TaskUpdateState.Success -> BorderStroke(
                    1.dp,
                    Color(0xFF10B981).copy(alpha = 0.7f)
                )

                is TaskDetailsViewModel.TaskUpdateState.Failure -> BorderStroke(
                    1.dp,
                    Color(0xFFEF4444).copy(alpha = 0.7f)
                )

                else -> BorderStroke(
                    0.5.dp,
                    MaterialTheme.colorScheme.outline.copy(0.3f)
                )
            }
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Row - Title and Checkbox
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            textDecoration = if (task.isCompleted)
                                TextDecoration.LineThrough
                            else TextDecoration.None
                        ),
                        fontWeight = FontWeight.Bold,
                        color = if (task.isCompleted)
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Checkbox with loading state
                    IconButton(
                        onClick = onToggleCompletion,
                        modifier = Modifier.size(32.dp),
                        enabled = taskUpdateState !is TaskDetailsViewModel.TaskUpdateState.Loading
                    ) {
                        when (taskUpdateState) {
                            is TaskDetailsViewModel.TaskUpdateState.Loading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.5.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            else -> {
                                Icon(
                                    imageVector = if (task.status == TaskStatus.COMPLETED)
                                        Icons.Filled.CheckCircle
                                    else Icons.Outlined.Circle,
                                    contentDescription = if (task.isCompleted) "Completed" else "Mark complete",
                                    tint = if (task.status == TaskStatus.COMPLETED)
                                        Color(0xFF10B981)
                                    else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                }

                // Show update status message
                if (taskUpdateState is TaskDetailsViewModel.TaskUpdateState.Failure) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = taskUpdateState.errorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFEF4444)
                        )
                    }
                }

                // Status Section with Dropdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Status",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )

                    Box {
                        Surface(
                            color = when (task.status) {
                                TaskStatus.NOT_STARTED -> MaterialTheme.colorScheme.surfaceVariant
                                TaskStatus.IN_PROGRESS -> Color(0xFFFEF3C7)
                                TaskStatus.ON_HOLD -> Color(0xFFFED7AA)
                                TaskStatus.REVIEW -> Color(0xFFDBEAFE)
                                TaskStatus.COMPLETED -> Color(0xFFDCFCE7)
                                TaskStatus.CANCELLED -> Color(0xFFFEE2E2)
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.clickable {
                                if (taskUpdateState !is TaskDetailsViewModel.TaskUpdateState.Loading) {
                                    showStatusDropdown = true
                                }
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(
                                    horizontal = 16.dp,
                                    vertical = 8.dp
                                ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (taskUpdateState is TaskDetailsViewModel.TaskUpdateState.Loading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        strokeWidth = 1.5.dp,
                                        color = when (task.status) {
                                            TaskStatus.NOT_STARTED -> MaterialTheme.colorScheme.onSurfaceVariant
                                            TaskStatus.IN_PROGRESS -> Color(0xFF92400E)
                                            TaskStatus.ON_HOLD -> Color(0xFF7C2D12)
                                            TaskStatus.REVIEW -> Color(0xFF1E40AF)
                                            TaskStatus.COMPLETED -> Color(0xFF166534)
                                            TaskStatus.CANCELLED -> Color(0xFF991B1B)
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }

                                Text(
                                    text = task.status.name.replace("_", " ").lowercase()
                                        .split(" ")
                                        .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = when (task.status) {
                                        TaskStatus.NOT_STARTED -> MaterialTheme.colorScheme.onSurfaceVariant
                                        TaskStatus.IN_PROGRESS -> Color(0xFF92400E)
                                        TaskStatus.ON_HOLD -> Color(0xFF7C2D12)
                                        TaskStatus.REVIEW -> Color(0xFF1E40AF)
                                        TaskStatus.COMPLETED -> Color(0xFF166534)
                                        TaskStatus.CANCELLED -> Color(0xFF991B1B)
                                    },
                                    fontWeight = FontWeight.Medium
                                )

                                if (taskUpdateState !is TaskDetailsViewModel.TaskUpdateState.Loading) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Change status",
                                        modifier = Modifier.size(18.dp),
                                        tint = when (task.status) {
                                            TaskStatus.NOT_STARTED -> MaterialTheme.colorScheme.onSurfaceVariant
                                            TaskStatus.IN_PROGRESS -> Color(0xFF92400E)
                                            TaskStatus.ON_HOLD -> Color(0xFF7C2D12)
                                            TaskStatus.REVIEW -> Color(0xFF1E40AF)
                                            TaskStatus.COMPLETED -> Color(0xFF166534)
                                            TaskStatus.CANCELLED -> Color(0xFF991B1B)
                                        }.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }

                        // Status Dropdown Menu
                        DropdownMenu(
                            expanded = showStatusDropdown,
                            onDismissRequest = { showStatusDropdown = false }
                        ) {
                            TaskStatus.values().forEach { status ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .background(
                                                        when (status) {
                                                            TaskStatus.NOT_STARTED -> MaterialTheme.colorScheme.surfaceVariant
                                                            TaskStatus.IN_PROGRESS -> Color(0xFFFEF3C7)
                                                            TaskStatus.ON_HOLD -> Color(0xFFFED7AA)
                                                            TaskStatus.REVIEW -> Color(0xFFDBEAFE)
                                                            TaskStatus.COMPLETED -> Color(0xFFDCFCE7)
                                                            TaskStatus.CANCELLED -> Color(0xFFFEE2E2)
                                                        },
                                                        CircleShape
                                                    )
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                text = status.name.replace("_", " ").lowercase()
                                                    .split(" ")
                                                    .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } },
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    },
                                    onClick = {
                                        onStatusChange(status)
                                        showStatusDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Description
                if (task.description.isNotEmpty()) {
                    Column {
                        Text(
                            text = "Description",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (task.isCompleted)
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            lineHeight = 24.sp
                        )
                    }
                }

                // Priority and Category
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Priority
                    if (task.priority.isNotEmpty()) {
                        Column {
                            Text(
                                text = "Priority",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                color = when (task.priority) {
                                    TaskPriority.HIGH.name -> Color(0xFFEF4444).copy(alpha = 0.1f)
                                    TaskPriority.MEDIUM.name -> Color(0xFFF59E0B).copy(alpha = 0.1f)
                                    TaskPriority.LOW.name -> Color(0xFF10B981).copy(alpha = 0.1f)
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = task.priority.lowercase()
                                        .replaceFirstChar { it.uppercase() },
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 6.dp
                                    ),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = when (task.priority) {
                                        TaskPriority.HIGH.name -> Color(0xFFEF4444)
                                        TaskPriority.MEDIUM.name -> Color(0xFFF59E0B)
                                        TaskPriority.LOW.name -> Color(0xFF10B981)
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Category
                    if (task.category.isNotEmpty()) {
                        Column {
                            Text(
                                text = "Category",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = task.category,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                val projectName by viewModel.getProjectNameFlow(task.projectId).collectAsState(initial = "")
                val moduleName by viewModel.getModuleNameFlow(task.moduleId).collectAsState(initial = "")

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (projectName?.isNotEmpty() == true) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Project:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                contentColor = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    text = projectName.orEmpty(),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    if (moduleName?.isNotEmpty() == true) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Module:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                contentColor = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    text = moduleName.orEmpty(),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Time Information
                val hasTimeInfo = task.estimatedTime.isNotEmpty() || task.dueDate.isNotEmpty()
                if (hasTimeInfo) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Estimated Time
                        if (task.estimatedTime.isNotEmpty()) {
                            Column {
                                Text(
                                    text = "Estimated Time",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Schedule,
                                        contentDescription = "Estimated time",
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = task.estimatedTime,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        // Due Date
                        if (task.dueDate.isNotEmpty()) {
                            Column {
                                Text(
                                    text = "Due Date",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.CalendarToday,
                                        contentDescription = "Due date",
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = task.dueDate,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                // Tags
                if (task.tags.isNotEmpty()) {
                    Column {
                        Text(
                            text = "Tags",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(task.tags) { tag ->
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "#$tag",
                                        modifier = Modifier.padding(
                                            horizontal = 10.dp,
                                            vertical = 4.dp
                                        ),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}