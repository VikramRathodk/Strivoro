package com.devvikram.striveo.config.mappers


import com.devvikram.striveo.room.model.RoomUser
import com.devvikram.striveo.config.constants.AppThemeMode
import com.devvikram.striveo.config.constants.PreferenceLanguage
import com.devvikram.striveo.config.constants.UserAccountType
import com.devvikram.striveo.firebase.models.FirebaseTask
import com.devvikram.striveo.firebase.models.MyFirebaseUser
import com.devvikram.striveo.room.model.RoomTask

object ModelMappers {

    fun mapToFirebaseUser(roomUser: RoomUser): MyFirebaseUser {
        return MyFirebaseUser(
            userId = roomUser.userId,
            name = roomUser.name,
            phone = roomUser.phone,
            email = roomUser.email,
            password = roomUser.password,
            avatarUrl = roomUser.avatarUrl,
            userType = roomUser.userType.name,
            isActive = roomUser.isActive,
            isEmailVerified = roomUser.isEmailVerified,
            preferredLanguage = roomUser.preferredLanguage.name,
            lastLoginAt = roomUser.lastLoginAt,
            lastActiveAt = roomUser.lastActiveAt,
            appThemeMode = roomUser.appThemeMode.name,
            deviceToken = roomUser.deviceToken,
            platform = roomUser.platform,
            createdAt = roomUser.createdAt,
            lastModifiedAt = roomUser.lastModifiedAt
        )
    }

    fun mapToRoomUser(myFirebaseUser: MyFirebaseUser): RoomUser {
        return RoomUser(
            userId = myFirebaseUser.userId,
            name = myFirebaseUser.name,
            phone = myFirebaseUser.phone,
            email = myFirebaseUser.email,
            avatarUrl = myFirebaseUser.avatarUrl,
            password = myFirebaseUser.password,
            userType = UserAccountType.fromString(myFirebaseUser.userType),
            isActive = myFirebaseUser.isActive,
            isEmailVerified = myFirebaseUser.isEmailVerified,
            preferredLanguage = PreferenceLanguage.fromCode(myFirebaseUser.preferredLanguage),
            lastLoginAt = myFirebaseUser.lastLoginAt,
            lastActiveAt = myFirebaseUser.lastActiveAt,
            appThemeMode = AppThemeMode.fromString(myFirebaseUser.appThemeMode),
            deviceToken = myFirebaseUser.deviceToken,
            platform = myFirebaseUser.platform,
            createdAt = myFirebaseUser.createdAt,
            lastModifiedAt = myFirebaseUser.lastModifiedAt
        )
    }

    fun toFirebaseTask(roomTask: RoomTask): FirebaseTask {
        return FirebaseTask(
            taskId = roomTask.taskId,
            title = roomTask.title,
            description = roomTask.description,
            category = roomTask.category,
            priority = roomTask.priority,
            estimatedTime = roomTask.estimatedTime,
            dueDate = roomTask.dueDate,
            isCompleted = roomTask.isCompleted,
            tags = roomTask.tags,
            createdAt = roomTask.createdAt,
            lastModifiedAt = roomTask.lastModifiedAt,
            createdBy = roomTask.createdBy
        )
    }

    fun toRoomTask(firebaseTask: FirebaseTask): RoomTask {
        return RoomTask(
            taskId = firebaseTask.taskId,
            title = firebaseTask.title,
            description = firebaseTask.description,
            category = firebaseTask.category,
            priority = firebaseTask.priority,
            estimatedTime = firebaseTask.estimatedTime,
            dueDate = firebaseTask.dueDate,
            isCompleted = firebaseTask.isCompleted,
            tags = firebaseTask.tags,
            createdAt = firebaseTask.createdAt,
            lastModifiedAt = firebaseTask.lastModifiedAt,
            createdBy = firebaseTask.createdBy
        )
    }

}
