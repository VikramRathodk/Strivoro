package com.devvikram.striveo.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeDashboardScreen(
    viewModel: HomeViewModel,
    onTaskClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredTasks by viewModel.filteredTasks.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val taskStats by viewModel.taskStats.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

//        item { ProgressSection(taskStats) }
        item {
            TaskFilterSection(selectedFilter) { filter ->
                viewModel.setFilter(filter)
            }
        }
        item {
            TasksSection(
                roomTasks = filteredTasks,
                filter = selectedFilter,
                onTaskToggle = { taskId ->
                    viewModel.toggleTask(taskId)
                },
                onTaskClick = onTaskClick,
                onStatusChange = { taskId, taskStatus ->
                    viewModel.updateTaskStatus(
                        taskId = taskId,
                        status = taskStatus
                    )
                },
                viewModel = viewModel
            )
        }
        item { WeeklySummarySection(taskStats) }
    }
}



