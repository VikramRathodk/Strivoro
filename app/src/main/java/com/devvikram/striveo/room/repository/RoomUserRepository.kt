package com.devvikram.striveo.room.repository

import com.devvikram.striveo.room.dao.RoomUserDao
import com.devvikram.striveo.room.model.RoomUser
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


class RoomUserRepository @Inject constructor(
    private val roomUserDao: RoomUserDao
) {


    // insert
    suspend fun insertUser(user: RoomUser) {
        roomUserDao.insertUser(user)
    }

    // get
    suspend fun getUserById(userId: String): RoomUser? {
        return roomUserDao.getUserById(userId)
    }

    // get by userid and flwo
    fun getUserByIdFlow(userId: String): Flow<RoomUser?> {
        return roomUserDao.getUserByIdFlow(userId)
    }

    // get list of users by ids and flow
    fun getUsersByIdsFlow(userIds: List<String>): Flow<List<RoomUser>> {
        return roomUserDao.getUsersByIdsFlow(userIds)
    }

    // get list of users by id and flow
    fun getAllUserByIdFlow(userId: String): Flow<List<RoomUser>> {
        return roomUserDao.getAllUserByIdFlow(userId)
    }

    // get all users
    suspend fun getAllUsers(): List<RoomUser> {
        return roomUserDao.getAllUsers()
    }


    // delete
    suspend fun deleteUserById(userId: String) {
        roomUserDao.deleteUserById(userId)
    }

    suspend fun deleteAllUsers() {
        roomUserDao.deleteAllUsers()
    }


}