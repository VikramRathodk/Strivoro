package com.devvikram.striveo.config.constants

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import androidx.core.content.edit

class LoginPreference @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("login_preferences", Context.MODE_PRIVATE)

    companion object {
        private const val IS_LOGGED_IN = "is_logged_in"
        private const val USER_ID = "user_id"
        private const val USERNAME = "username"
        private const val USER_EMAIL = "user_email"
        private const val AUTH_TOKEN = "auth_token"
        private const val REMEMBER_ME = "remember_me"
        private const val LAST_LOGIN_TIME = "last_login_time"
        private const val ONBOARDING_COMPLETED = "onboarding_completed"
    }

    // Save login state
    fun saveLoginState(
        isLoggedIn: Boolean,
        userId: String = "",
        username: String = "",
        email: String = "",
        authToken: String = "",
        rememberMe: Boolean = false,
        lastLoginTime: Long = 0L
    ) {
        prefs.edit().apply {
            putBoolean(IS_LOGGED_IN, isLoggedIn)
            putString(USER_ID, userId)
            putString(USERNAME, username)
            putString(USER_EMAIL, email)
            putString(AUTH_TOKEN, authToken)
            putBoolean(REMEMBER_ME, rememberMe)
            putLong(LAST_LOGIN_TIME, lastLoginTime)
            apply()
        }
    }

    // Accessors
    val isLoggedIn: Boolean
        get() = prefs.getBoolean(IS_LOGGED_IN, false)

    val userId: String
        get() = prefs.getString(USER_ID, "") ?: ""

    val username: String
        get() = prefs.getString(USERNAME, "") ?: ""

    val userEmail: String
        get() = prefs.getString(USER_EMAIL, "") ?: ""

    val authToken: String
        get() = prefs.getString(AUTH_TOKEN, "") ?: ""

    val rememberMe: Boolean
        get() = prefs.getBoolean(REMEMBER_ME, false)

    val lastLoginTime: Long
        get() = prefs.getLong(LAST_LOGIN_TIME, 0L)

    // Clear all login data (logout)
    fun clearLoginData() {
        prefs.edit { clear() }
    }

    // Update specific preference
    fun updateAuthToken(token: String) {
        prefs.edit().putString(AUTH_TOKEN, token).apply()
    }

    fun updateRememberMe(remember: Boolean) {
        prefs.edit().putBoolean(REMEMBER_ME, remember).apply()
    }

    fun isOnboardingCompleted(): Boolean {
        return prefs.getBoolean( ONBOARDING_COMPLETED, false)
    }

    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit { putBoolean( ONBOARDING_COMPLETED, completed) }
    }

    // Consolidated accessor
    val userData: UserData
        get() = UserData(
            isLoggedIn = isLoggedIn,
            userId = userId,
            username = username,
            email = userEmail,
            authToken = authToken,
            rememberMe = rememberMe,
            lastLoginTime = lastLoginTime
        )
}

// Data class to represent the user's login session
data class UserData(
    val isLoggedIn: Boolean = false,
    val userId: String = "",
    val username: String = "",
    val email: String = "",
    val authToken: String = "",
    val rememberMe: Boolean = false,
    val lastLoginTime: Long = 0L
)
