package com.devvikram.striveo.firebase.repository

import android.util.Log
import com.devvikram.striveo.config.constants.App
import com.devvikram.striveo.firebase.models.FirebaseTask
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseTaskRepository @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore
) {

    private val tasksCollection = firebaseFirestore.collection(App.FIREBASE_COLLECTION_TASKS)

    suspend fun saveTask(task: FirebaseTask): Result<Unit> {
        return try {
            if (task.taskId.isBlank()) {
                return Result.failure(IllegalArgumentException("Task ID cannot be empty"))
            }
            if (task.title.isBlank()) {
                return Result.failure(IllegalArgumentException("Task title cannot be empty"))
            }

            tasksCollection.document(task.taskId).set(task).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseTaskRepository", "Failed to save task: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun updateTaskFields(
        taskId: String,
        field: Map<String, Any>,
        onSuccessListener: (String) -> Unit,
        onFailedListener: (String) -> Unit
    ) {
        try {
            tasksCollection.document(taskId).update(field).await()
            onSuccessListener("Updated Successfully")
        } catch (e: Exception) {
            Log.e("FirebaseTaskRepository", "Failed to update task: ${e.message}", e)
            onFailedListener(e.message.toString())
        }
    }
}
