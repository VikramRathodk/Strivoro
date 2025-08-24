package com.devvikram.striveo.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devvikram.striveo.config.constants.LoginPreference
import com.devvikram.striveo.config.enums.TaskFilter
import com.devvikram.striveo.config.enums.TaskPriority
import com.devvikram.striveo.room.model.RoomTask
import com.devvikram.striveo.room.repository.RoomTaskRepository
import com.devvikram.striveo.ui.TaskStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val loginPreference: LoginPreference,
    private val roomTaskRepository: RoomTaskRepository
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(TaskFilter.TODAY)
    val selectedFilter: StateFlow<TaskFilter> = _selectedFilter.asStateFlow()

    private val _tasks = MutableStateFlow<List<RoomTask>>(emptyList())
    val allTasks: StateFlow<List<RoomTask>> = _tasks.asStateFlow()

    val filteredTasks: StateFlow<List<RoomTask>> = combine(
        _tasks,
        _selectedFilter
    ) { taskList, filter ->
        when (filter) {
            TaskFilter.ALL -> taskList
            TaskFilter.TODAY -> taskList.filter {
                it.dueDate == "Today" || it.dueDate.contains("AM") || it.dueDate.contains("PM")
            }
            TaskFilter.UPCOMING -> taskList.filter {
                it.dueDate == "Tomorrow" || it.dueDate == "This Week"
            }
            TaskFilter.COMPLETED -> taskList.filter { it.isCompleted }
            TaskFilter.OVERDUE -> taskList.filter {
                !it.isCompleted && it.dueDate == "Yesterday"
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val taskStats: StateFlow<TaskStats> = _tasks.map { taskList ->
        TaskStats(
            totalTasks = taskList.size,
            completedTasks = taskList.count { it.isCompleted },
            pendingTasks = taskList.count { !it.isCompleted },
            overdueTasks = taskList.count { !it.isCompleted && it.dueDate == "Yesterday" },
            focusTime = "4.2h",
            productivity = if (taskList.isEmpty()) 0 else (taskList.count { it.isCompleted } * 100) / taskList.size,
            weeklyCompletion = 85
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TaskStats(0, 0, 0, 0, "0h", 0, 0)
    )

    val greeting: String
        get() {
            val currentTime = SimpleDateFormat("HH", Locale.getDefault()).format(Date()).toInt()
            return when {
                currentTime < 12 -> "Good Morning"
                currentTime < 17 -> "Good Afternoon"
                else -> "Good Evening"
            }
        }

    init {
        // Initialize and observe tasks from repository
        observeTasks()
    }

    private fun observeTasks() {
        viewModelScope.launch {
            roomTaskRepository.getAllTasks().collect { tasks ->
                _tasks.value = tasks
            }
        }
    }

    fun toggleTask(taskId: String) {
        viewModelScope.launch {
            try {
                // Find the task from current tasks
                val currentTasks = _tasks.value
                val taskToUpdate = currentTasks.find { it.taskId == taskId }

                taskToUpdate?.let { task ->
                    val updatedTask = task.copy(
                        isCompleted = !task.isCompleted,
                        lastModifiedAt = System.currentTimeMillis()
                    )
                    roomTaskRepository.updateTask(updatedTask)
                }
            } catch (e: Exception) {
                // Handle error if needed
                e.printStackTrace()
            }
        }
    }

    fun setFilter(filter: TaskFilter) {
        _selectedFilter.value = filter
    }

    override fun onCleared() {
        super.onCleared()
        // Cleanup if needed
    }
}