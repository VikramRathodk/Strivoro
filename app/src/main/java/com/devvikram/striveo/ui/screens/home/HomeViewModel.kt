package com.devvikram.striveo.ui.screens.home

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devvikram.striveo.config.constants.LoginPreference
import com.devvikram.striveo.config.enums.TaskFilter
import com.devvikram.striveo.config.enums.TaskPriority
import com.devvikram.striveo.config.enums.TaskStatus
import com.devvikram.striveo.firebase.repository.FirebaseTaskRepository
import com.devvikram.striveo.room.model.RoomTask
import com.devvikram.striveo.room.repository.RoomProjectRepository
import com.devvikram.striveo.room.repository.RoomTaskRepository
import com.devvikram.striveo.ui.TaskStats
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val loginPreference: LoginPreference,
    private val roomTaskRepository: RoomTaskRepository,
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseTaskRepository: FirebaseTaskRepository,
    private val roomProjectRepository: RoomProjectRepository

) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(TaskFilter.TODAY)
    val selectedFilter: StateFlow<TaskFilter> = _selectedFilter.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()


    private val _taskUpdateStates = mutableStateMapOf<String, TaskUpdateState>()
    val taskUpdateStates: Map<String, TaskUpdateState> = _taskUpdateStates

    fun getTaskUpdateState(taskId: String): TaskUpdateState {
        return _taskUpdateStates[taskId] ?: TaskUpdateState.Idle
    }

    val allTasks: StateFlow<List<RoomTask>> = roomTaskRepository.getAllTasks()
        .catch { exception ->
            _error.value = "Failed to load tasks: ${exception.message}"
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val filteredTasks: StateFlow<List<RoomTask>> = combine(
        allTasks,
        _selectedFilter
    ) { taskList, filter ->
        filterTasks(taskList, filter)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val taskStats: StateFlow<TaskStats> = allTasks.map { taskList ->
        calculateTaskStats(taskList)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TaskStats(0, 0, 0, 0, "0h", 0, 0)
    )

    val greeting: String
        get() = getTimeBasedGreeting()

    private fun filterTasks(taskList: List<RoomTask>, filter: TaskFilter): List<RoomTask> {
        return when (filter) {
            TaskFilter.ALL -> taskList
            TaskFilter.TODAY -> taskList.filter { task ->
                isTaskForToday(task.createdAt)
            }

            TaskFilter.UPCOMING -> taskList.filter { task ->
                isTaskUpcoming(task.createdAt)
            }

            TaskFilter.COMPLETED -> taskList.filter { it.isCompleted }
            TaskFilter.OVERDUE -> taskList.filter { task ->
                !task.isCompleted && isTaskOverdue(task.createdAt)
            }
        }
    }

    private fun calculateTaskStats(taskList: List<RoomTask>): TaskStats {
        val completedCount = taskList.count { it.isCompleted }
        val pendingCount = taskList.count { !it.isCompleted }
        val overdueCount = taskList.count { !it.isCompleted && isTaskOverdue(it.createdAt) }

        val productivity = if (taskList.isEmpty()) 0 else (completedCount * 100) / taskList.size

        return TaskStats(
            totalTasks = taskList.size,
            completedTasks = completedCount,
            pendingTasks = pendingCount,
            overdueTasks = overdueCount,
            focusTime = calculateFocusTime(taskList),
            productivity = productivity,
            weeklyCompletion = calculateWeeklyCompletion(taskList)
        )
    }

    private fun calculateFocusTime(taskList: List<RoomTask>): String {
        val completedTasksToday = taskList.count {
            it.isCompleted && isTaskForToday(it.createdAt)
        }
        val estimatedHours = completedTasksToday * 0.5
        return String.format("%.1fh", estimatedHours)
    }

    private fun calculateWeeklyCompletion(taskList: List<RoomTask>): Int {
        val thisWeekTasks = taskList.filter { isTaskThisWeek(it.createdAt) }
        val completedThisWeek = thisWeekTasks.count { it.isCompleted }

        return if (thisWeekTasks.isEmpty()) 0
        else (completedThisWeek * 100) / thisWeekTasks.size
    }

    private fun isTaskForToday(timeStamp: Long): Boolean {
        val calTask = Calendar.getInstance().apply { timeInMillis = timeStamp }
        val calToday = Calendar.getInstance()

        return calTask.get(Calendar.YEAR) == calToday.get(Calendar.YEAR) &&
                calTask.get(Calendar.DAY_OF_YEAR) == calToday.get(Calendar.DAY_OF_YEAR)
    }

    private fun isTaskUpcoming(dueDate: Long): Boolean {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        return dueDate > today
    }

    private fun isTaskOverdue(dueDate: Long): Boolean {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis

        return dueDate < today
    }

    private fun isTaskThisWeek(timeStamp: Long): Boolean {
        val cal = Calendar.getInstance()
        val weekOfYear = cal.get(Calendar.WEEK_OF_YEAR)
        val year = cal.get(Calendar.YEAR)

        val calTask = Calendar.getInstance().apply { timeInMillis = timeStamp }

        return calTask.get(Calendar.WEEK_OF_YEAR) == weekOfYear &&
                calTask.get(Calendar.YEAR) == year
    }
    private fun getTimeBasedGreeting(): String {
        val currentTime = SimpleDateFormat("HH", Locale.getDefault()).format(Date()).toInt()
        return when {
            currentTime < 12 -> "Good Morning"
            currentTime < 17 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }

    fun toggleTask(taskId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val task = roomTaskRepository.getTaskById(taskId)

                task?.let { currentTask ->
                    val updatedTask = currentTask.copy(
                        isCompleted = !currentTask.isCompleted,
                        lastModifiedAt = System.currentTimeMillis()
                    )
                    roomTaskRepository.updateTask(updatedTask)
                } ?: run {
                    _error.value = "Task not found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to update task: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setFilter(filter: TaskFilter) {
        _selectedFilter.value = filter
        _error.value = null
    }

    fun clearError() {
        _error.value = null
    }

    fun refreshTasks() {
        // The flow will automatically refresh, but you can add manual refresh logic if needed
        viewModelScope.launch {
            try {
                _isLoading.value = true
                // Force refresh from repository if needed
                // roomTaskRepository.refreshTasks()
            } catch (e: Exception) {
                _error.value = "Failed to refresh tasks: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // StateFlows are automatically cleaned up with viewModelScope
    }

    fun updateTaskStatus(taskId: String, status: TaskStatus) {
        viewModelScope.launch {
            _taskUpdateStates[taskId] = TaskUpdateState.Loading
            roomTaskRepository.updateTaskStatus(taskId, status)
            firebaseTaskRepository.updateTaskFields(
                taskId = taskId,
                field = mapOf(
                    "status" to status,
                    "isCompleted" to (status == TaskStatus.COMPLETED),
                    "lastModifiedAt" to System.currentTimeMillis()
                ),
                onSuccessListener = { it->
                    _taskUpdateStates[taskId] = TaskUpdateState.Success(it)
                },
                onFailedListener = {  it ->
                    _taskUpdateStates[taskId] = TaskUpdateState.Failure(it ?: "Update failed")
                }
            )
        }
    }

    fun getProjectNameFlow(projectId: String): Flow<String> {
        return roomProjectRepository.getProjectByIdFlow(projectId)
            .map { it?.projectName ?: "" }
    }




    sealed class TaskUpdateState {
        object Idle : TaskUpdateState()

        object Loading : TaskUpdateState()

        data class Success(
            val message : String
        ) : TaskUpdateState()
        data class Failure(val errorMessage: String) : TaskUpdateState()
    }

}