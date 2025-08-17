package com.devvikram.striveo.room.dao

import androidx.room.*
import com.devvikram.striveo.room.model.RoomTask
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: RoomTask)

    @Update
    suspend fun updateTask(task: RoomTask)

    @Delete
    suspend fun deleteTask(task: RoomTask)

    @Query("SELECT * FROM tasks WHERE taskId = :id")
    suspend fun getTaskById(id: String): RoomTask?

    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<RoomTask>>

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()
}
