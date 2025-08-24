package com.devvikram.striveo.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devvikram.striveo.config.constants.App
import com.devvikram.striveo.config.constants.LoginPreference
import com.devvikram.striveo.config.mappers.ModelMappers
import com.devvikram.striveo.firebase.models.FirebaseTask
import com.devvikram.striveo.firebase.models.MyFirebaseUser
import com.devvikram.striveo.firebase.repository.FirebaseTaskRepository
import com.devvikram.striveo.firebase.repository.FirebaseUserRepository
import com.devvikram.striveo.room.repository.RoomTaskRepository
import com.devvikram.striveo.room.repository.RoomUserRepository
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewmodel @Inject constructor(
    private val loginPreference: LoginPreference,
    private val roomUserRepository: RoomUserRepository,
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseUserRepository: FirebaseUserRepository,
    private val firebaseTaskRepository: FirebaseTaskRepository,
    private val roomTaskRepository: RoomTaskRepository
): ViewModel() {

    private val _loginState = MutableStateFlow<Boolean>(false)
    val loginState: StateFlow<Boolean> = _loginState.asStateFlow()

    private val _onboardingState = MutableStateFlow<Boolean>(false)
    val onboardingState: StateFlow<Boolean>  = _onboardingState.asStateFlow()

    private val userCollection = firebaseFirestore.collection(App.FIREBASE_COLLECTION_USERS)
    private val taskCollection = firebaseFirestore.collection(App.FIREBASE_COLLECTION_TASKS)

    init {
        _loginState.value = loginPreference.isLoggedIn()
        _onboardingState.value = loginPreference.isOnboardingCompleted()
        listenToContactChanges()
        listenToTaskCollection()
    }

    private fun listenToContactChanges() {

        userCollection
            .addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Handle error
                return@addSnapshotListener
            }

            viewModelScope.launch {
                if (snapshot != null) {
                    for (document in snapshot.documents) {
                        val myFirebaseUser = document.toObject(MyFirebaseUser::class.java)
                        if (myFirebaseUser != null) {
                            roomUserRepository.insertUser(ModelMappers.mapToRoomUser(myFirebaseUser))
                        }
                    }
                }
            }

        }
    }

    private fun listenToTaskCollection() {
        taskCollection
            .whereEqualTo("createdBy", loginPreference.getUserId())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FirestoreListener", "Error listening to task collection", error)
                    return@addSnapshotListener
                }

                if (snapshot == null) {
                    Log.d("FirestoreListener", "Snapshot is null")
                    return@addSnapshotListener
                }

                viewModelScope.launch {
                    for (documentChange in snapshot.documentChanges) {
                        try {
                            val document = documentChange.document
                            val firebaseTask = document.toObject(FirebaseTask::class.java)

                            if (firebaseTask == null) {
                                Log.w("FirestoreListener", "Failed to convert document to FirebaseTask: ${document.id}")
                                continue
                            }

                            val roomTask = ModelMappers.toRoomTask(firebaseTask)

                            when (documentChange.type) {
                                DocumentChange.Type.ADDED -> {
                                    Log.d("FirestoreListener", "Task added: ${roomTask.taskId}")
                                    roomTaskRepository.insertTask(roomTask)
                                }

                                DocumentChange.Type.MODIFIED -> {
                                    Log.d("FirestoreListener", "Task modified: ${roomTask.taskId}")
                                    roomTaskRepository.updateTask(roomTask)
                                }

                                DocumentChange.Type.REMOVED -> {
                                    Log.d("FirestoreListener", "Task removed: ${roomTask.taskId}")
                                    roomTaskRepository.deleteTaskById(roomTask.taskId)
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("FirestoreListener", "Failed to process document change: ${documentChange.document.id}", e)
                        }
                    }
                }
            }
    }


    fun logout() {
        loginPreference.clearLoginData()
        _loginState.value = false

        viewModelScope.launch {
            roomUserRepository.deleteAllUsers()
            roomTaskRepository.deleteAllTasks()
        }
    }

    fun setOnboardingComplete() {
        loginPreference.setOnboardingCompleted(true)
        _onboardingState.value = true
    }

    fun setOnboardingInComplete() {
        loginPreference.setOnboardingCompleted(false)
        _onboardingState.value = false
    }

}