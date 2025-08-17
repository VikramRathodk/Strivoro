package com.devvikram.striveo.ui.screens.calender

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devvikram.striveo.config.enums.TaskPriority
import com.devvikram.striveo.room.model.RoomTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import java.util.UUID

@HiltViewModel
class CalenderViewModel @Inject constructor() : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth.asStateFlow()

    private val _tasks = MutableStateFlow<List<RoomTask>>(emptyList())
    val tasks: StateFlow<List<RoomTask>> = _tasks.asStateFlow()

    private val _tasksForSelectedDate = MutableStateFlow<List<RoomTask>>(emptyList())
    val tasksForSelectedDate: StateFlow<List<RoomTask>> = _tasksForSelectedDate.asStateFlow()

    private val _filteredPriority = MutableStateFlow<String?>(null)

    init {
        loadTasks()
        updateTasksForSelectedDate()
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        updateTasksForSelectedDate()
    }

    fun navigateToMonth(month: YearMonth) {
        _currentMonth.value = month
    }

    fun navigateToPreviousMonth() {
        _currentMonth.value = _currentMonth.value.minusMonths(1)
    }

    fun navigateToNextMonth() {
        _currentMonth.value = _currentMonth.value.plusMonths(1)
    }

    fun jumpToMonth(year: Int, month: Int) {
        _currentMonth.value = YearMonth.of(year, month)
    }

    fun filterTasks(priority: String) {
        _filteredPriority.value = if (priority == "All") null else priority
        updateTasksForSelectedDate()
    }

    fun addTask(
        title: String,
        description: String,
        category: String,
        priority: TaskPriority,
        estimatedTime: String,
        dueDate: LocalDate,
        tags: List<String>
    ) {
        viewModelScope.launch {
            val newRoomTask = RoomTask(
                taskId = UUID.randomUUID().toString(),
                title = title,
                description = description,
                category = category,
                priority = priority.name,
                estimatedTime = estimatedTime,
                dueDate = dueDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                isCompleted = false,
                tags = tags,
                lastModifiedAt = System.currentTimeMillis()
            )
            _tasks.value = _tasks.value + newRoomTask
            updateTasksForSelectedDate()
        }
    }

    private fun updateTasksForSelectedDate() {
        val selectedDateString = _selectedDate.value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        var filteredTasks = _tasks.value.filter { task ->
            task.dueDate.startsWith(selectedDateString)
        }

        _filteredPriority.value?.let { priority ->
            filteredTasks = filteredTasks.filter { task ->
                when (priority) {
                    "High" -> task.priority == TaskPriority.HIGH.name
                    "Medium" -> task.priority == TaskPriority.MEDIUM.name
                    "Low" -> task.priority == TaskPriority.LOW.name
                    else -> true
                }
            }
        }

        _tasksForSelectedDate.value = filteredTasks
    }

    private fun loadTasks() {
        viewModelScope.launch {
            // Sample tasks - replace with actual data source
            val sampleRoomTasks = listOf(
                RoomTask(
                    taskId = "1",
                    title = "Team Meeting",
                    description = "Weekly team sync meeting",
                    category = "Work",
                    priority = TaskPriority.HIGH.name,
                    estimatedTime = "1h",
                    dueDate = "2025-06-21 10:00",
                    isCompleted = false,
                    tags = listOf("meeting", "work"),
                    lastModifiedAt = System.currentTimeMillis()
                ),
                RoomTask(
                    taskId = "2",
                    title = "Code Review",
                    description = "Review PR for calendar feature",
                    category = "Development",
                    priority = TaskPriority.MEDIUM.name,
                    estimatedTime = "30m",
                    dueDate = "2025-06-21 14:00",
                    isCompleted = false,
                    tags = listOf("code", "review")
                ),
                RoomTask(
                    taskId = "3",
                    title = "Gym Workout",
                    description = "Upper body workout",
                    category = "Personal",
                    priority = TaskPriority.LOW.name,
                    estimatedTime = "45m",
                    dueDate = "2025-06-22 18:00",
                    isCompleted = false,
                    tags = listOf("fitness", "health")
                ),
                RoomTask(
                    taskId = "4",
                    title = "Project Deadline",
                    description = "Submit final project deliverables",
                    category = "Work",
                    priority = TaskPriority.HIGH.name,
                    estimatedTime = "2h",
                    dueDate = "2025-06-25 17:00",
                    isCompleted = false,
                    tags = listOf("deadline", "project")
                )
            )
            _tasks.value = sampleRoomTasks
            updateTasksForSelectedDate()
        }
    }

    fun getTasksForDate(date: LocalDate): List<RoomTask> {
        val dateString = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        var filteredTasks = _tasks.value.filter { task ->
            task.dueDate.startsWith(dateString)
        }

        _filteredPriority.value?.let { priority ->
            filteredTasks = filteredTasks.filter { task ->
                when (priority) {
                    "High" -> task.priority == TaskPriority.HIGH.name
                    "Medium" -> task.priority == TaskPriority.MEDIUM.name
                    "Low" -> task.priority == TaskPriority.LOW.name
                    else -> true
                }
            }
        }

        return filteredTasks
    }

    fun toggleTaskCompletion(taskId: String) {
        _tasks.value = _tasks.value.map { task ->
            if (task.taskId == taskId) {
                task.copy(isCompleted = !task.isCompleted)
            } else {
                task
            }
        }
        updateTasksForSelectedDate()
    }
}