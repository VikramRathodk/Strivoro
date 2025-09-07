package com.devvikram.striveo.ui.screens.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devvikram.striveo.config.constants.App
import com.devvikram.striveo.config.constants.LoginPreference
import com.devvikram.striveo.config.mappers.ModelMappers
import com.devvikram.striveo.firebase.repository.FirebaseProjectRepository
import com.devvikram.striveo.room.model.RoomProject
import com.devvikram.striveo.room.repository.RoomProjectRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val loginPreference: LoginPreference,
    private val firebaseProjectRepository: FirebaseProjectRepository,
    private val roomProjectRepository: RoomProjectRepository,
    private val firebaseFirestore: FirebaseFirestore
) : ViewModel() {

    private val _projectName = MutableStateFlow("")
    val projectName = _projectName.asStateFlow()

    private val _projectDescription = MutableStateFlow("")
    val projectDescription = _projectDescription.asStateFlow()

    private val _addProjectUiState = MutableStateFlow<AddProjectUiState>(AddProjectUiState.Idle)
    val addProjectUiState = _addProjectUiState.asStateFlow()

    private val _updateProjectUiState = MutableStateFlow<UpdateProjectUiState>(UpdateProjectUiState.Idle)
    val updateProjectUiState = _updateProjectUiState.asStateFlow()

    private val _projectsUiState = MutableStateFlow<ProjectsUiState>(ProjectsUiState.Loading)
    val projectsUiState = _projectsUiState.asStateFlow()

    private val _showAddProjectDialog = MutableStateFlow(false)
    val showAddProjectDialog = _showAddProjectDialog.asStateFlow()

    init {
        loadProjects()
    }

    private fun loadProjects() {
        viewModelScope.launch {
            roomProjectRepository.getAllProjectsFlow()
                .catch { exception ->
                    _projectsUiState.value =
                        ProjectsUiState.Error(exception.message ?: "Unknown error")
                }
                .collect { projects ->
                    _projectsUiState.value = if (projects.isEmpty()) {
                        ProjectsUiState.Empty
                    } else {
                        ProjectsUiState.Success(projects)
                    }
                }
        }
    }

    fun addProject() {

        if (projectName.value.isBlank()) {
            _addProjectUiState.value = AddProjectUiState.Error("Project name cannot be empty")
            return
        }

        if (projectDescription.value.length > 200) {
            _addProjectUiState.value =
                AddProjectUiState.Error("Project description cannot exceed 200 characters")
            return
        }

        if (projectName.value.length > 50) {
            _addProjectUiState.value =
                AddProjectUiState.Error("Project name cannot exceed 50 characters")
            return
        }
        val projectId = firebaseFirestore.collection(App.FIREBASE_COLLECTION_PROJECTS).document().id
        val currentLoggedUsername = loginPreference.getUsername()


        val roomProject = RoomProject(
            projectName = _projectName.value,
            description = _projectDescription.value,
            createdAt = System.currentTimeMillis(),
            lastModifiedAt = System.currentTimeMillis(),
            projectId = projectId,
            createdBy = currentLoggedUsername,
            status = "active",
        )

        viewModelScope.launch {
            _addProjectUiState.value = AddProjectUiState.Loading
            try {
                roomProjectRepository.addOrUpdateProject(roomProject)
                firebaseProjectRepository.addProject(
                    ModelMappers.mapToFirebaseProject(roomProject),
                    onSuccess = {
                        _addProjectUiState.value =
                            AddProjectUiState.Success("Project added successfully")
                        resetAddProjectForm()
                        hideAddProjectDialog()
                    },
                    onFailure = { error ->
                        _addProjectUiState.value = AddProjectUiState.Error(error.message)
                    }
                )
            } catch (e: Exception) {
                _addProjectUiState.value = AddProjectUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            try {
                roomProjectRepository.deleteProjectById(projectId)
                // Also delete from Firebase if needed
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateProjectName(name: String) {
        _projectName.value = name
    }

    fun updateProjectDescription(description: String) {
        _projectDescription.value = description
    }

    fun showAddProjectDialog() {
        _showAddProjectDialog.value = true
    }

    fun hideAddProjectDialog() {
        _showAddProjectDialog.value = false
        resetAddProjectState()
    }

    private fun resetAddProjectForm() {
        _projectName.value = ""
        _projectDescription.value = ""
    }

    fun resetAddProjectState() {
        _addProjectUiState.value = AddProjectUiState.Idle
        resetAddProjectForm()
    }

    fun updateProject(it: RoomProject) {
        viewModelScope.launch {
            try {

                roomProjectRepository.addOrUpdateProject(it.copy(
                    lastModifiedAt = System.currentTimeMillis()
                ))
                firebaseProjectRepository.updateField(
                    projectId = it.projectId,
                    fields = mapOf(
                        "projectName" to it.projectName,
                        "description" to it.description,
                        "lastModifiedAt" to System.currentTimeMillis(),
                    ),
                    onSuccess = {
                        _updateProjectUiState.value =
                            UpdateProjectUiState.Success("Project updated successfully")
                        resetAddProjectForm()
                        hideAddProjectDialog()
                    },
                    onFailure = { error ->
                        _updateProjectUiState.value = UpdateProjectUiState.Error(error.message)
                    }
                )
            } catch (e: Exception) {
                _updateProjectUiState.value = UpdateProjectUiState.Error(e.message ?: "Unknown error")
            }

        }
    }

    fun resetUpdateProjectState() {
        _updateProjectUiState.value = UpdateProjectUiState.Idle
    }

    // UI States
    sealed class AddProjectUiState {
        object Idle : AddProjectUiState()
        object Loading : AddProjectUiState()
        data class Success(val message: String) : AddProjectUiState()
        data class Error(val message: String?) : AddProjectUiState()
    }

    sealed class UpdateProjectUiState {
        object Idle : UpdateProjectUiState()
        object Loading : UpdateProjectUiState()
        data class Success(val message: String) : UpdateProjectUiState()
        data class Error(val message: String?) : UpdateProjectUiState()
    }

    sealed class ProjectsUiState {
        object Loading : ProjectsUiState()
        object Empty : ProjectsUiState()
        data class Success(val projects: List<RoomProject>) : ProjectsUiState()
        data class Error(val message: String) : ProjectsUiState()
    }
}