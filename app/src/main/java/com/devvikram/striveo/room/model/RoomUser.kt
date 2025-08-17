package com.devvikram.striveo.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.devvikram.striveo.config.constants.AppThemeMode
import com.devvikram.striveo.config.constants.PreferenceLanguage
import com.devvikram.striveo.config.constants.UserAccountType

@Entity(tableName = "users")
data class RoomUser(
    @PrimaryKey()
    val userId: String = "", // Unique ID from Firebase Authentication

    val name: String = "", // User's full name
    val phone: String = "", // User's phone number
    val email: String = "", // User's email address
    val password: String = "", // User's password

    val avatarUrl: String? = null, //  profile picture URL

    val userType: UserAccountType = UserAccountType.PERSONAL, // Type of account: PERSONAL, ORGANIZATION, ORGANIZATION, EXTERNAL, TEAM, ENTERPRISE, INVITED.

    val isActive: Boolean = true, // Whether the account is currently active
    val isEmailVerified: Boolean = false, // Whether the user's email has been verified

    val preferredLanguage: PreferenceLanguage = PreferenceLanguage.ENGLISH,

    val lastLoginAt: Long? = null, // Timestamp of the last successful login
    val lastActiveAt: Long? = null, // Timestamp of the user's last activity in the app

    val appThemeMode: AppThemeMode = AppThemeMode.LIGHT, // Whether the user prefers dark mode UI

    val deviceToken: String? = null, // Push notification device token (FCM)
    val platform: String = "android", // User's device platform: android, ios, or web

    val createdAt: Long = System.currentTimeMillis(), // Timestamp when the user account was created
    val lastModifiedAt: Long = System.currentTimeMillis(), // Timestamp of last update to the user record
    val allowNotifications: Boolean = true // Whether the user wants to receive push notifications
)