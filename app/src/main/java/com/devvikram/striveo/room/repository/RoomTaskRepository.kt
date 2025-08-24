package com.devvikram.striveo.room.repository


import com.devvikram.striveo.room.dao.TaskDao
import com.devvikram.striveo.room.model.RoomTask
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomTaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {
    suspend fun insertTask(task: RoomTask) {
        if (task.taskId.isNotBlank()) {
            taskDao.insertTask(task)
        } else {
            throw IllegalArgumentException("Task ID must not be blank")
        }
    }

    suspend fun updateTask(task: RoomTask) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: RoomTask) {
        taskDao.deleteTask(task)
    }

    suspend fun getTaskById(taskId: String): RoomTask? {
        return taskDao.getTaskById(taskId)
    }

    fun getAllTasks(): Flow<List<RoomTask>> {
        return taskDao.getAllTasks()
    }

    suspend fun deleteAllTasks() {
        taskDao.deleteAllTasks()
    }

   suspend fun deleteTaskById(taskId: String) {
        taskDao.deleteTaskById(taskId)
    }
}
