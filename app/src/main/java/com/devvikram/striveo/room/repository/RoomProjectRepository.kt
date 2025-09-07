package com.devvikram.striveo.room.repository

import com.devvikram.striveo.room.dao.RoomProjectDao
import com.devvikram.striveo.room.model.RoomProject
import javax.inject.Inject

class RoomProjectRepository @Inject constructor(
    private val roomProjectDao: RoomProjectDao
) {

    // Suspend functions are used to run them in a background thread
    suspend fun addOrUpdateProject(roomProject: RoomProject) {
        roomProjectDao.insertProject(roomProject)
    }

    suspend fun getProjectById(projectId: String): RoomProject? {
        return roomProjectDao.getProjectById(projectId)
    }

    suspend fun getProjectsByUserId(userId: String): List<RoomProject> {
        return roomProjectDao.getProjectsByUserId(userId)
    }

    suspend fun getAllProjects(): List<RoomProject> {
        return roomProjectDao.getAllProjects()
    }

    suspend fun deleteProjectById(projectId: String) {
        roomProjectDao.deleteProjectById(projectId)
    }

    suspend fun deleteAllProjects() {
        roomProjectDao.deleteAllProjects()
    }

    // flow is used to observe changes in the data

    fun getProjectByIdFlow(projectId: String) = roomProjectDao.getProjectByIdFlow(projectId)

    fun getProjectsByUserIdFlow(userId: String) = roomProjectDao.getProjectsByUserIdFlow(userId)

    fun getAllProjectsFlow() = roomProjectDao.getAllProjectsFlow()

}