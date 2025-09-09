package com.devvikram.striveo.ui.screens.tasks.details

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devvikram.striveo.config.constants.LoginPreference
import com.devvikram.striveo.config.enums.TaskStatus
import com.devvikram.striveo.firebase.repository.FirebaseTaskRepository
import com.devvikram.striveo.room.model.RoomTask
import com.devvikram.striveo.room.repository.RoomModuleRepository
import com.devvikram.striveo.room.repository.RoomProjectRepository
import com.devvikram.striveo.room.repository.RoomTaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailsViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val loginPreference: LoginPreference,
    private val roomTaskRepository: RoomTaskRepository,
    private val roomProjectRepository: RoomProjectRepository,
    private val roomModuleRepository: RoomModuleRepository,
    private val firebaseTaskRepository: FirebaseTaskRepository
) : ViewModel() {

    private val _taskId = MutableStateFlow<String?>(null)
    val taskId: StateFlow<String?> = _taskId.asStateFlow()

    private val _taskDetailState = MutableStateFlow<TaskDetailState>(TaskDetailState.Idle)
    val taskDetailState: StateFlow<TaskDetailState> = _taskDetailState.asStateFlow()

    private val _taskUpdateState = MutableStateFlow<TaskUpdateState>(TaskUpdateState.Idle)
    val taskUpdateState: StateFlow<TaskUpdateState> = _taskUpdateState.asStateFlow()

    fun setTaskId(id: String?) {
        _taskId.value = id
    }

    fun updateTaskStatus(taskId: String, newStatus: TaskStatus) {
        viewModelScope.launch {
            _taskUpdateState.value = TaskUpdateState.Loading
            roomTaskRepository.updateTaskStatus(taskId, newStatus)
            firebaseTaskRepository.updateTaskFields(
                taskId = taskId,
                field = mapOf(
                    "status" to newStatus,
                    "isCompleted" to (newStatus == TaskStatus.COMPLETED),
                    "lastModifiedAt" to System.currentTimeMillis()
                ),
                onSuccessListener = { it->
                    _taskUpdateState.value = TaskUpdateState.Success("Task status updated successfully")
                },
                onFailedListener = {  it ->
                    _taskUpdateState.value = TaskUpdateState.Failure("Failed to update task status")
                }
            )
        }
    }

    fun toggleTaskCompletion(taskId: String) {

    }

    fun getProjectNameFlow(projectId: String): Flow<String> {
        return roomProjectRepository.getProjectByIdFlow(projectId)
            .map { it?.projectName ?: "" }
    }
    fun getModuleNameFlow(moduleId: String): Flow<String> {
        return roomModuleRepository.getModuleByIdFlow(moduleId)
            .map { it?.title ?: "" }
    }


    init {
        viewModelScope.launch {
            _taskId.collect { id ->
                if (id != null) {
                    _taskDetailState.value = TaskDetailState.Loading
                    roomTaskRepository.getTaskByIdFlow(id).collect { task ->
                        if (task != null) {
                            _taskDetailState.value = TaskDetailState.Success(task)
                        } else {
                            _taskDetailState.value = TaskDetailState.Error("Task not found")
                        }
                    }
                }
            }
        }
    }

    sealed class TaskDetailState {
        object Idle : TaskDetailState()
        object Loading : TaskDetailState()
        data class Success(val task: RoomTask) : TaskDetailState()
        data class Error(val message: String) : TaskDetailState()
    }

    sealed class TaskUpdateState {
        object Idle : TaskUpdateState()
        object Loading : TaskUpdateState()
        data class Success(val message: String) : TaskUpdateState()
        data class Failure(val errorMessage: String) : TaskUpdateState()
    }


}