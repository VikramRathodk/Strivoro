package com.devvikram.striveo.ui.screens.modules

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devvikram.striveo.config.constants.App
import com.devvikram.striveo.config.constants.LoginPreference
import com.devvikram.striveo.config.mappers.ModelMappers
import com.devvikram.striveo.firebase.repository.FirebaseModuleRepository
import com.devvikram.striveo.room.model.RoomModule
import com.devvikram.striveo.room.model.RoomProject
import com.devvikram.striveo.room.repository.RoomModuleRepository
import com.devvikram.striveo.room.repository.RoomProjectRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class ModuleViewModel @Inject constructor(
    private val roomModuleRepository: RoomModuleRepository,
    private val firebaseModuleRepository: FirebaseModuleRepository,
    private val roomProjectRepository: RoomProjectRepository,
    private val firebaseFirestore: FirebaseFirestore,
    private val loginPreference: LoginPreference
) : ViewModel() {

    private val _moduleState = MutableStateFlow<ModuleState>(ModuleState.Idle)
    val moduleState: StateFlow<ModuleState> = _moduleState.asStateFlow()

    private val _projects = MutableStateFlow<List<RoomProject>>(emptyList())
    val projects: StateFlow<List<RoomProject>> = _projects.asStateFlow()

    init {
        loadModules()
        viewModelScope.launch {
            roomProjectRepository.getAllProjectsFlow().collectLatest { list ->
                _projects.value = list
            }
        }
    }

    private fun loadModules() {
        viewModelScope.launch {
            _moduleState.value = ModuleState.Loading
            roomModuleRepository.getAllModulesFlow()
                .catch { exception ->
                    _moduleState.value = ModuleState.Error(
                        exception.message ?: "Unknown error occurred"
                    )
                }
                .collectLatest { modules ->
                    _moduleState.value = ModuleState.Success(modules)
                }
        }
    }


    // Add a new module
    fun addModule(module: RoomModule) {
        viewModelScope.launch {
            try {
                val moduleId =
                    firebaseFirestore.collection(App.FIREBASE_COLLECTION_MODULES).document().id

                val module = module.copy(
                    moduleId = moduleId,
                    lastModifiedAt = System.currentTimeMillis(),
                    createdAt = System.currentTimeMillis(),
                    createdBy = loginPreference.getUserId()
                )

                roomModuleRepository.addOrUpdateModule(module)
                firebaseModuleRepository.addOrUpdateModule(
                    module = ModelMappers.mapToFirebaseModule(module),
                    onSuccess = {

                    },
                    onFailure = { exception ->
                        // Handle failure
                    }
                )
            } catch (exception: Exception) {
                _moduleState.value = ModuleState.Error(
                    exception.message ?: "Failed to add module"
                )
            }
        }
    }

    // Update an existing module
    fun updateModule(module: RoomModule) {
        viewModelScope.launch {
            try {
                roomModuleRepository.addOrUpdateModule(module.copy(lastModifiedAt = System.currentTimeMillis()))
                firebaseModuleRepository.updateFields(
                    moduleId = module.moduleId,
                    fields = mapOf(
                        "title" to module.title,
                        "description" to module.description,
                        "lastModifiedAt" to System.currentTimeMillis()
                    ),
                    onSuccess = {
                        // Handle success
                        Log.d(TAG, "updateModule:" + module.moduleId + "success")
                    },
                    onFailure = { exception ->
                        // Handle failure
                        Log.d(TAG, "updateModule:" + module.moduleId + "failure")
                    }
                )
            } catch (exception: Exception) {
                _moduleState.value = ModuleState.Error(
                    exception.message ?: "Failed to update module"
                )
            }
        }
    }

    // Delete a module
    fun deleteModule(moduleId: String) {
        viewModelScope.launch {
            try {
                roomModuleRepository.deleteModuleById(moduleId)
            } catch (exception: Exception) {
                _moduleState.value = ModuleState.Error(
                    exception.message ?: "Failed to delete module"
                )
            }
        }
    }

    fun getProjects(): List<RoomProject> {
        return projects.value
    }


    sealed class ModuleState {
        object Idle : ModuleState()
        object Loading : ModuleState()
        data class Success(val modules: List<RoomModule>) : ModuleState()
        data class Error(val message: String) : ModuleState()
    }
}