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

    fun getUserId(): String {
        return prefs.getString(USER_ID, "") ?: ""
    }

    fun getUsername(): String {
        return prefs.getString(USERNAME, "") ?: ""
    }

    fun getEmail(): String {
        return prefs.getString(USER_EMAIL, "") ?: ""
    }

    fun getAuthToken(): String {
        return prefs.getString(AUTH_TOKEN, "") ?: ""
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        prefs.edit { putBoolean(IS_LOGGED_IN, isLoggedIn) }
    }

    fun getRememberMe(): Boolean {
        return prefs.getBoolean(REMEMBER_ME, false)
    }

    fun getLastLoginTime(): Long {
        return prefs.getLong(LAST_LOGIN_TIME, 0L)
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(IS_LOGGED_IN, false)
    }

    // Clear only login-related data (preserve onboarding state)
    fun clearLoginData() {
        // Store onboarding state before clearing
        val onboardingCompleted = isOnboardingCompleted()

        prefs.edit {
            // Clear all data
            clear()
            // Restore onboarding state
            putBoolean(ONBOARDING_COMPLETED, onboardingCompleted)
        }
    }

    // Alternative method: Clear only login-specific keys (more explicit)
    fun clearLoginDataSelective() {
        prefs.edit {
            remove(IS_LOGGED_IN)
            remove(USER_ID)
            remove(USERNAME)
            remove(USER_EMAIL)
            remove(AUTH_TOKEN)
            remove(REMEMBER_ME)
            remove(LAST_LOGIN_TIME)
            // Keep ONBOARDING_COMPLETED intact
        }
    }

    // Update specific preference
    fun updateAuthToken(token: String) {
        prefs.edit { putString(AUTH_TOKEN, token) }
    }

    fun updateRememberMe(remember: Boolean) {
        prefs.edit { putBoolean(REMEMBER_ME, remember) }
    }

    fun isOnboardingCompleted(): Boolean {
        return prefs.getBoolean(ONBOARDING_COMPLETED, false)
    }

    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit { putBoolean(ONBOARDING_COMPLETED, completed) }
    }

    // Get current user data
    fun getCurrentUserData(): UserData {
        return UserData(
            isLoggedIn = isLoggedIn(),
            userId = getUserId(),
            username = getUsername(),
            email = getEmail(),
            authToken = getAuthToken(),
            rememberMe = getRememberMe(),
            lastLoginTime = getLastLoginTime()
        )
    }

    fun completeReset() {
        prefs.edit { clear() }
    }
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