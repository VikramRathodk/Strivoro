package com.devvikram.striveo.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.devvikram.striveo.room.model.RoomUser
import kotlinx.coroutines.flow.Flow


@Dao
interface RoomUserDao  {

    @Upsert
    suspend fun insertUser(user: RoomUser)

    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserById(userId: String): RoomUser?

    @Query("DELETE FROM users WHERE userId = :userId")
    suspend fun deleteUserById(userId: String)

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<RoomUser>

    //by id flow
    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getUserByIdFlow(userId: String): Flow<RoomUser?>

    @Query("SELECT * FROM users WHERE userId = :userId")
     fun getAllUserByIdFlow(userId: String): Flow<List<RoomUser>>

    //list of user by id and flow
    @Query("SELECT * FROM users WHERE userId IN (:userIds)")
    fun getUsersByIdsFlow(userIds: List<String>): Flow<List<RoomUser>>

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()


}