package com.devvikram.striveo.ui.screens.calender

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devvikram.striveo.config.enums.TaskPriority
import com.devvikram.striveo.room.model.RoomTask

import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalenderScreen(
    viewModel: CalenderViewModel = hiltViewModel(),
    onAddTask: (LocalDate) -> Unit
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val currentMonth by viewModel.currentMonth.collectAsState()
    val tasksForSelectedDate by viewModel.tasksForSelectedDate.collectAsState()
    var isWeekView by remember { mutableStateOf(false) }
    var showFilterMenu by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Calendar Header
            CalendarHeader(
                currentMonth = currentMonth,
                onPreviousMonth = viewModel::navigateToPreviousMonth,
                onNextMonth = viewModel::navigateToNextMonth,
                onMonthYearSelect = { year, month -> viewModel.jumpToMonth(year, month) },
                isWeekView = isWeekView,
                onToggleView = { isWeekView = !isWeekView }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Filter Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { showFilterMenu = true }) {
                    Icon(Icons.Default.FilterList, contentDescription = "Filter Tasks")
                }
                DropdownMenu(
                    expanded = showFilterMenu,
                    onDismissRequest = { showFilterMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("All") },
                        onClick = {
                            selectedFilter = "All"
                            showFilterMenu = false
                            viewModel.filterTasks("All")
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("High Priority") },
                        onClick = {
                            selectedFilter = "High"
                            showFilterMenu = false
                            viewModel.filterTasks("High")
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Medium Priority") },
                        onClick = {
                            selectedFilter = "Medium"
                            showFilterMenu = false
                            viewModel.filterTasks("Medium")
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Low Priority") },
                        onClick = {
                            selectedFilter = "Low"
                            showFilterMenu = false
                            viewModel.filterTasks("Low")
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calendar Grid
            if (isWeekView) {
                WeekView(
                    selectedDate = selectedDate,
                    onDateSelected = viewModel::selectDate,
                    getTasksForDate = viewModel::getTasksForDate
                )
            } else {
                CalendarGrid(
                    currentMonth = currentMonth,
                    selectedDate = selectedDate,
                    onDateSelected = viewModel::selectDate,
                    getTasksForDate = viewModel::getTasksForDate
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tasks for Selected Date
            TasksSection(
                selectedDate = selectedDate,
                roomTasks = tasksForSelectedDate,
                onTaskToggle = viewModel::toggleTaskCompletion
            )

            // Add Task Button
            FloatingActionButton(
                onClick = { onAddTask(selectedDate) },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 16.dp),
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Task",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun CalendarHeader(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onMonthYearSelect: (Int, Int) -> Unit,
    isWeekView: Boolean,
    onToggleView: () -> Unit
) {
    var showMonthPicker by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onPreviousMonth,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Icon(
                Icons.Default.ChevronLeft,
                contentDescription = "Previous Month",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Text(
            text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.clickable { showMonthPicker = true }
        )

        IconButton(
            onClick = onNextMonth,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Next Month",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }

    // Month/Year Picker
    DropdownMenu(
        expanded = showMonthPicker,
        onDismissRequest = { showMonthPicker = false }
    ) {
        val years = (currentMonth.year - 5..currentMonth.year + 5).toList()
        val months = (1..12).toList()
        years.forEach { year ->
            months.forEach { month ->
                DropdownMenuItem(
                    text = { Text("${YearMonth.of(year, month).format(DateTimeFormatter.ofPattern("MMM yyyy"))}") },
                    onClick = {
                        onMonthYearSelect(year, month)
                        showMonthPicker = false
                    }
                )
            }
        }
    }

    // Week/Month View Toggle
    Button(
        onClick = onToggleView,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(if (isWeekView) "Switch to Month View" else "Switch to Week View")
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun CalendarGrid(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    getTasksForDate: (LocalDate) -> List<RoomTask>
) {
    val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val firstDayOfMonth = currentMonth.atDay(1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
    val daysInMonth = currentMonth.lengthOfMonth()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Days of week header
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                items(daysOfWeek) { day ->
                    Text(
                        text = day,
                        modifier = Modifier
                            .width(48.dp)
                            .padding(vertical = 8.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calendar days grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.height(320.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Empty cells for days before first day of month
                items(firstDayOfWeek) {
                    Box(modifier = Modifier.size(48.dp))
                }

                // Days of the month
                items(daysInMonth) { day ->
                    val date = currentMonth.atDay(day + 1)
                    val tasks = getTasksForDate(date)
                    val isSelected = date == selectedDate
                    val isToday = date == LocalDate.now()

                    CalendarDay(
                        day = day + 1,
                        isSelected = isSelected,
                        isToday = isToday,
                        roomTasks = tasks,
                        onClick = { onDateSelected(date) }
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun WeekView(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    getTasksForDate: (LocalDate) -> List<RoomTask>
) {
    val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val startOfWeek = selectedDate.minusDays(selectedDate.dayOfWeek.value % 7L)
    val weekDays = (0..6).map { startOfWeek.plusDays(it.toLong()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Days of week header
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                items(daysOfWeek) { day ->
                    Text(
                        text = day,
                        modifier = Modifier
                            .width(48.dp)
                            .padding(vertical = 8.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Week days
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(weekDays) { date ->
                    val tasks = getTasksForDate(date)
                    val isSelected = date == selectedDate
                    val isToday = date == LocalDate.now()

                    CalendarDay(
                        day = date.dayOfMonth,
                        isSelected = isSelected,
                        isToday = isToday,
                        roomTasks = tasks,
                        onClick = { onDateSelected(date) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarDay(
    day: Int,
    isSelected: Boolean,
    isToday: Boolean,
    roomTasks: List<RoomTask>,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isSelected -> MaterialTheme.colorScheme.primary
            isToday -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(200)
    )

    val hasHighPriority = roomTasks.any { it.priority == TaskPriority.HIGH.name }
    val hasMediumPriority = roomTasks.any { it.priority == TaskPriority.MEDIUM.name }
    val hasLowPriority = roomTasks.any { it.priority == TaskPriority.LOW.name }

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(
                width = if (hasHighPriority) 2.dp else 0.dp,
                color = if (hasHighPriority) Color.Red else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .shadow(if (isSelected) 4.dp else 0.dp, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = day.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
                color = when {
                    isSelected -> MaterialTheme.colorScheme.onPrimary
                    isToday -> MaterialTheme.colorScheme.onPrimaryContainer
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )

            Row(
                modifier = Modifier.padding(top = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                if (hasHighPriority) {
                    Icon(
                        imageVector = Icons.Default.Circle,
                        contentDescription = null,
                        modifier = Modifier.size(6.dp),
                        tint = Color.Red
                    )
                }
                if (hasMediumPriority) {
                    Icon(
                        imageVector = Icons.Default.Circle,
                        contentDescription = null,
                        modifier = Modifier.size(6.dp),
                        tint = Color.Cyan
                    )
                }
                if (hasLowPriority) {
                    Icon(
                        imageVector = Icons.Default.Circle,
                        contentDescription = null,
                        modifier = Modifier.size(6.dp),
                        tint = Color.Green
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun TasksSection(
    selectedDate: LocalDate,
    roomTasks: List<RoomTask>,
    onTaskToggle: (String) -> Unit
) {
    Column {
        Text(
            text = "Tasks for ${selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (roomTasks.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    text = "No tasks for this date",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(roomTasks) { task ->
                    TaskItem(
                        roomTask = task,
                        onToggle = { onTaskToggle(task.taskId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskItem(
    roomTask: RoomTask,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = if (roomTask.isCompleted)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = roomTask.isCompleted,
                onCheckedChange = { onToggle() },
                modifier = Modifier.padding(end = 12.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = roomTask.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (roomTask.isCompleted)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface
                )

                if (roomTask.description.isNotEmpty()) {
                    Text(
                        text = roomTask.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = roomTask.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = roomTask.estimatedTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Priority indicator
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(
                        color = when (roomTask.priority) {
                            TaskPriority.HIGH.name -> Color.Red
                            TaskPriority.MEDIUM.name -> Color.Cyan
                            TaskPriority.LOW.name -> Color.Green
                            else -> {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        },
                        shape = CircleShape
                    )
            )
        }
    }
}