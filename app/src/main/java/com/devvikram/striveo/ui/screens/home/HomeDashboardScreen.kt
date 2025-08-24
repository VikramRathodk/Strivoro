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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devvikram.striveo.config.constants.LoginPreference
import com.devvikram.striveo.room.dao.TaskDao
import com.devvikram.striveo.room.model.RoomTask
import com.devvikram.striveo.room.repository.RoomTaskRepository

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
                onTaskClick = onTaskClick
            )
        }
        item { WeeklySummarySection(taskStats) }
    }
}

@Preview
@Composable
fun HomeDashboardScreenPreview() {
    val taskDao = object : TaskDao {
        override suspend fun insertTask(task: RoomTask) {}
        override suspend fun updateTask(task: RoomTask) {}
        override suspend fun deleteTask(task: RoomTask) {}
        override suspend fun getTaskById(id: String): RoomTask? = null
        override fun getAllTasks(): kotlinx.coroutines.flow.Flow<List<RoomTask>> =
            kotlinx.coroutines.flow.flowOf(emptyList())

        override suspend fun deleteAllTasks() {}
        override suspend fun deleteTaskById(taskId: String) {
        }
    }
    val roomTaskRepository = RoomTaskRepository(taskDao)
    val viewModel = HomeViewModel(
        loginPreference = LoginPreference(LocalContext.current),
        roomTaskRepository = roomTaskRepository
    )
    HomeDashboardScreen(
        viewModel = viewModel,
        onTaskClick = {})
}
