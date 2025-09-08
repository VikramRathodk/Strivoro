package com.devvikram.striveo.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.devvikram.striveo.room.model.RoomModule

@Dao
interface RoomModuleDao {

    @Upsert
    suspend fun insertModule(module: RoomModule)

    @Upsert
    suspend fun insertModules(modules: List<RoomModule>)


    @Query("SELECT * FROM modules WHERE module_id = :moduleId")
    suspend fun getModuleById(moduleId: String): RoomModule?

    @Query("SELECT * FROM modules WHERE project_id = :projectId")
    suspend fun getModulesByProjectId(projectId: String): List<RoomModule>

    @Query("DELETE FROM modules WHERE module_id = :moduleId")
    suspend fun deleteModuleById(moduleId: String)

    @Query("DELETE FROM modules WHERE project_id = :projectId")
    suspend fun deleteModulesByProjectId(projectId: String)

    @Query("DELETE FROM modules")
    suspend fun deleteAllModules()

    // flow
    @Query("SELECT * FROM modules WHERE module_id = :moduleId")
    fun getModuleByIdFlow(moduleId: String): kotlinx.coroutines.flow.Flow<RoomModule?>

    @Query("SELECT * FROM modules WHERE project_id = :projectId")
    fun getModulesByProjectIdFlow(projectId: String): kotlinx.coroutines.flow.Flow<List<RoomModule>>

    @Query("SELECT * FROM modules")
    fun getAllModulesFlow(): kotlinx.coroutines.flow.Flow<List<RoomModule>>



}