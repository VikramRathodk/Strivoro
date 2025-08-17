package com.devvikram.striveo.firebase.models

import com.devvikram.striveo.config.constants.AppThemeMode
import com.devvikram.striveo.config.constants.PreferenceLanguage
import com.devvikram.striveo.config.constants.UserAccountType

data class MyFirebaseUser(
    val userId: String = "",                  // Unique Firebase Auth UID
    val name: String = "",                    // Full name
    val phone: String = "",                   // Phone number
    val email: String = "",                   // Email address
    val avatarUrl: String? = null,            // Profile picture URL

    val userType: String = UserAccountType.PERSONAL.name,        // Stored as String for compatibility: PERSONAL, ORGANIZATION, etc.
    val isActive: Boolean = true,             // Is the account active
    val isEmailVerified: Boolean = false,     // Email verification status

    val preferredLanguage: String = PreferenceLanguage.ENGLISH.code,

    val lastLoginAt: Long? = null,            // Last login timestamp
    val lastActiveAt: Long? = null,           // Last active timestamp

    val appThemeMode: String = AppThemeMode.LIGHT.name,       // Theme preference: LIGHT, DARK, SYSTEM, etc.

    val deviceToken: String? = null,          // FCM push notification token
    val platform: String = "android",         // User platform: android, ios, web

    val createdAt: Long = System.currentTimeMillis(),      // Account creation timestamp
    val lastModifiedAt: Long = System.currentTimeMillis()  // Last modification timestamp
)
