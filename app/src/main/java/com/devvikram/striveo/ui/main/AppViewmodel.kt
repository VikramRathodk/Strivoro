package com.devvikram.striveo.ui.main

import android.util.Log
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
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private val userCollection = firebaseFirestore.collection(App.FIREBASE_COLLECTION_USERS)
    private val taskCollection = firebaseFirestore.collection(App.FIREBASE_COLLECTION_TASKS)

    init {
        listenToContactChanges()
        listenToTaskCollection()
    }

    private fun listenToContactChanges() {
//        val loggedUserId = loginPreference.userId

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
//            .whereEqualTo("createdBy", loginPreference.userId)
            .addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Log the error
                Log.e("FirestoreListener", "Error listening to task collection", error)
                return@addSnapshotListener
            }

            if (snapshot == null || snapshot.isEmpty) {
                Log.d("FirestoreListener", "No task documents found.")
                return@addSnapshotListener
            }

            viewModelScope.launch {
                for (document in snapshot.documents) {
                    try {
                        val firebaseTask = document.toObject(FirebaseTask::class.java)
                        firebaseTask?.let {
                            val roomTask = ModelMappers.toRoomTask(it)
                            roomTaskRepository.insertTask(roomTask)
                        }
                    } catch (e: Exception) {
                        Log.e("FirestoreListener", "Failed to map or insert task: ${document.id}", e)
                    }
                }
            }
        }
    }


    fun logout() {
        loginPreference.clearLoginData()
        viewModelScope.launch {
            roomUserRepository.deleteAllUsers()
        }
    }

}