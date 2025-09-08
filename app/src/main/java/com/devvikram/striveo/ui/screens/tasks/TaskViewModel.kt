package com.devvikram.striveo.ui.screens.tasks

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devvikram.striveo.config.constants.App
import com.devvikram.striveo.config.constants.LoginPreference
import com.devvikram.striveo.config.enums.TaskPriority
import com.devvikram.striveo.config.enums.TaskStatus
import com.devvikram.striveo.config.mappers.ModelMappers
import com.devvikram.striveo.firebase.repository.FirebaseTaskRepository
import com.devvikram.striveo.room.model.RoomProject
import com.devvikram.striveo.room.model.RoomTask
import com.devvikram.striveo.room.repository.RoomProjectRepository
import com.devvikram.striveo.room.repository.RoomTaskRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val loginPreference: LoginPreference,
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseTaskRepository: FirebaseTaskRepository,
    private val roomTaskRepository: RoomTaskRepository,
    private val roomProjectRepository: RoomProjectRepository
) : ViewModel() {

    companion object {
        private const val TAG = "TaskViewModel"
    }
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Task creation form states
    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _category = MutableStateFlow("")
    val category: StateFlow<String> = _category.asStateFlow()

    private val _priority = MutableStateFlow(TaskPriority.MEDIUM)
    val priority: StateFlow<TaskPriority> = _priority.asStateFlow()

    private val _tags = MutableStateFlow<List<String>>(emptyList())
    val tags: StateFlow<List<String>> = _tags.asStateFlow()

    private val _currentTag = MutableStateFlow("")
    val currentTag: StateFlow<String> = _currentTag.asStateFlow()

    private val _validationErrors = MutableStateFlow<List<String>>(emptyList())
    val validationErrors: StateFlow<List<String>> = _validationErrors.asStateFlow()

    private val _projects = MutableStateFlow<List<RoomProject>>(emptyList())
    val projects: StateFlow<List<RoomProject>> = _projects.asStateFlow()

    private val _selectedProject = MutableStateFlow<RoomProject?>(null)
    val selectedProject: StateFlow<RoomProject?> = _selectedProject.asStateFlow()

    init {
        viewModelScope.launch {
            roomProjectRepository.getAllProjectsFlow().collectLatest { list ->
                _projects.value = list
            }
        }
    }

    fun getProjectList (): List<RoomProject> {
        return projects.value
    }
    fun setSelectedProject(project: RoomProject?) {
        _selectedProject.value = project
    }


    // Predefined categories and suggestions
    val predefinedCategories = listOf(
        "Work", "Personal", "Health", "Finance", "Education",
        "Shopping", "Travel", "Home", "Fitness", "Learning"
    )

    val suggestedTags = listOf(
        "urgent", "important", "meeting", "deadline", "review",
        "research", "planning", "creative", "routine", "follow-up"
    )

    fun updateTitle(newTitle: String) {
        _title.value = newTitle
        clearValidationErrors()
    }

    fun updateDescription(newDescription: String) {
        _description.value = newDescription
    }

    fun updateCategory(newCategory: String) {
        _category.value = newCategory
        clearValidationErrors()
    }

    fun updatePriority(newPriority: TaskPriority) {
        _priority.value = newPriority
    }


    fun updateCurrentTag(tag: String) {
        _currentTag.value = tag
    }

    fun addTag() {
        val tag = _currentTag.value.trim()
        if (tag.isNotEmpty() && !_tags.value.contains(tag)) {
            _tags.value = _tags.value + tag
            _currentTag.value = ""
        }
    }

    fun removeTag(tag: String) {
        _tags.value = _tags.value.filter { it != tag }
    }

    fun addSuggestedTag(tag: String) {
        if (!_tags.value.contains(tag)) {
            _tags.value = _tags.value + tag
        }
    }

    private fun clearValidationErrors() {
        _validationErrors.value = emptyList()
    }

    fun createTask() {
        if (validateForm()) {
            _uiState.value = UiState.Loading
            viewModelScope.launch {
                try {
                    val taskId = firebaseFirestore.collection(App.FIREBASE_COLLECTION_TASKS).document().id

                    val roomTask = RoomTask(
                        taskId = taskId,
                        title = _title.value.trim(),
                        description = _description.value.trim(),
                        category = _category.value.trim(),
                        priority = _priority.value.name,
                        estimatedTime = "",
                        dueDate = "",
                        isCompleted = false,
                        status = TaskStatus.NOT_STARTED,
                        tags = _tags.value,
                        lastModifiedAt = System.currentTimeMillis(),
                        createdAt = System.currentTimeMillis(),
                        createdBy = loginPreference.getUserId(),
                        projectId = _selectedProject.value?.projectId ?: ""
                    )
                    roomTaskRepository.insertTask(roomTask)
                    val result =
                        firebaseTaskRepository.saveTask(ModelMappers.toFirebaseTask(roomTask))
                    result.onFailure {
                        _uiState.value = UiState.Error("Failed to create task. Please try again.")
                        _validationErrors.value = listOf("Failed to create task. Please try again.")
                        return@launch
                    }.onSuccess {
                        _uiState.value = UiState.Success("Task created successfully!")
                    }

                    // Reset form after successful creation
                    resetForm()

                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d(TAG, "createTask: Exception ${e.message}")
                    _uiState.value = UiState.Error("Failed to create task. Please try again.")
                    _validationErrors.value = listOf("Failed to create task. Please try again.")
                }
            }
        }
    }

    private fun validateForm(): Boolean {
        val errors = mutableListOf<String>()

        if (_title.value.trim().isEmpty()) {
            errors.add("Title is required")
        }

        if (_category.value.trim().isEmpty()) {
            errors.add("Category is required")
        }


        _validationErrors.value = errors
        return errors.isEmpty()
    }


    fun resetForm() {
        _title.value = ""
        _description.value = ""
        _category.value = ""
        _priority.value = TaskPriority.MEDIUM
        _tags.value = emptyList()
        _currentTag.value = ""
        _validationErrors.value = emptyList()
    }

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val message: String) : UiState()
        data class Error(val message: String) : UiState()

    }


}