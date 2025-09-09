package com.devvikram.striveo.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.devvikram.striveo.room.model.RoomProject
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomProjectDao  {

    // Suspend functions are used to run them in a background thread
    @Upsert
    suspend fun insertProject(roomProject: RoomProject)

    @Query("SELECT * FROM projects WHERE project_id = :projectId")
    suspend fun getProjectById(projectId: String): RoomProject?

    @Query("SELECT * FROM projects WHERE created_by = :userId")
    suspend fun getProjectsByUserId(userId: String): List<RoomProject>

    @Query("SELECT * FROM projects")
    suspend fun getAllProjects(): List<RoomProject>

    @Query("DELETE FROM projects WHERE project_id = :projectId")
    suspend fun deleteProjectById(projectId: String)

    @Query("DELETE FROM projects")
    suspend fun deleteAllProjects()

    // flow is used to observe changes in the data

    @Query("SELECT * FROM projects WHERE project_id = :projectId")
    fun getProjectByIdFlow(projectId: String): Flow<RoomProject?>

    @Query("SELECT * FROM projects WHERE created_by = :userId")
    fun getProjectsByUserIdFlow(userId: String): Flow<List<RoomProject>>

    @Query("SELECT * FROM projects")
    fun getAllProjectsFlow(): Flow<List<RoomProject>>


}