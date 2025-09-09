package com.devvikram.striveo.room.repository

import com.devvikram.striveo.room.dao.RoomModuleDao
import com.devvikram.striveo.room.model.RoomModule
import javax.inject.Inject

class RoomModuleRepository @Inject constructor(
    private val roomModuleDao: RoomModuleDao
) {
    suspend fun addOrUpdateModule(module: RoomModule) {
        roomModuleDao.insertModule(module)
    }

    suspend fun getModuleById(moduleId: String): RoomModule? {
        return roomModuleDao.getModuleById(moduleId)
    }

    suspend fun getModulesByProjectId(projectId: String): List<RoomModule> {
        return roomModuleDao.getModulesByProjectId(projectId)
    }



    suspend fun deleteModuleById(moduleId: String) {
        roomModuleDao.deleteModuleById(moduleId)
    }
    suspend fun deleteModulesByProjectId(projectId: String) {
        roomModuleDao.deleteModulesByProjectId(projectId)
    }


    suspend fun deleteAllModules() {
        roomModuleDao.deleteAllModules()
    }

    fun getModuleByIdFlow(moduleId: String) = roomModuleDao.getModuleByIdFlow(moduleId)

    fun getModulesByProjectIdFlow(projectId: String) = roomModuleDao.getModulesByProjectIdFlow(projectId)

    fun getAllModulesFlow() = roomModuleDao.getAllModulesFlow()



}