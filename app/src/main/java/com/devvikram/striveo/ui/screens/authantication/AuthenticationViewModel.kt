package com.devvikram.striveo.ui.screens.authantication

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devvikram.striveo.config.constants.LoginPreference
import com.devvikram.striveo.config.mappers.ModelMappers
import com.devvikram.striveo.firebase.models.MyFirebaseUser
import com.devvikram.striveo.firebase.repository.FirebaseUserRepository
import com.devvikram.striveo.room.repository.RoomUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Sealed class for authentication states
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Authenticated(val user: MyFirebaseUser) : AuthState()
    data class Error(val message: String) : AuthState()
    object Unauthenticated : AuthState()
}

// Form validation errors
data class FormErrors(
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
)

// UI State with sealed class
data class AuthUiState(
    val authState: AuthState = AuthState.Idle,
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val formErrors: FormErrors = FormErrors()
) {
    val isLoading: Boolean get() = authState is AuthState.Loading
    val isAuthenticated: Boolean get() = authState is AuthState.Authenticated
    val errorMessage: String? get() = (authState as? AuthState.Error)?.message
    val user: MyFirebaseUser? get() = (authState as? AuthState.Authenticated)?.user
}

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val loginPreference: LoginPreference,
    private val firebaseUserRepository: FirebaseUserRepository,
    private val roomUserRepository: RoomUserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        val isAuthenticated = firebaseUserRepository.isUserAuthenticatedInFirebase()
        _uiState.value = _uiState.value.copy(
            authState = if (isAuthenticated) AuthState.Unauthenticated else AuthState.Idle
        )
    }

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            formErrors = _uiState.value.formErrors.copy(emailError = null),
            authState = if (_uiState.value.authState is AuthState.Error) AuthState.Idle else _uiState.value.authState
        )
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            formErrors = _uiState.value.formErrors.copy(passwordError = null),
            authState = if (_uiState.value.authState is AuthState.Error) AuthState.Idle else _uiState.value.authState
        )
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(
            confirmPassword = confirmPassword,
            formErrors = _uiState.value.formErrors.copy(confirmPasswordError = null),
            authState = if (_uiState.value.authState is AuthState.Error) AuthState.Idle else _uiState.value.authState
        )
    }

    private fun validateLoginForm(): Boolean {
        val currentState = _uiState.value

        val emailError = when {
            currentState.email.isBlank() -> "Email is required"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(currentState.email)
                .matches() -> "Invalid email format"
            else -> null
        }

        val passwordError = when {
            currentState.password.isBlank() -> "Password is required"
            currentState.password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }

        val hasErrors = emailError != null || passwordError != null

        if (hasErrors) {
            _uiState.value = currentState.copy(
                formErrors = FormErrors(
                    emailError = emailError,
                    passwordError = passwordError
                )
            )
        }

        return !hasErrors
    }

    private fun validateRegistrationForm(): Boolean {
        val currentState = _uiState.value

        val emailError = when {
            currentState.email.isBlank() -> "Email is required"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(currentState.email)
                .matches() -> "Invalid email format"
            else -> null
        }

        val passwordError = when {
            currentState.password.isBlank() -> "Password is required"
            currentState.password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }

        val confirmPasswordError = when {
            currentState.confirmPassword.isBlank() -> "Please confirm your password"
            currentState.password != currentState.confirmPassword -> "Passwords do not match"
            else -> null
        }

        val hasErrors = emailError != null || passwordError != null || confirmPasswordError != null

        if (hasErrors) {
            _uiState.value = currentState.copy(
                formErrors = FormErrors(
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmPasswordError
                )
            )
        }

        return !hasErrors
    }

    fun login() {
        if (!validateLoginForm()) return

        val currentState = _uiState.value
        viewModelScope.launch {
            _uiState.value = currentState.copy(authState = AuthState.Loading)

            try {
                Log.d(TAG, "login: attempting login with email: ${currentState.email}")

                withContext(Dispatchers.IO) {
                    firebaseUserRepository.signInWithEmailAndPassword(
                        currentState.email,
                        currentState.password,
                        onSuccess = { firebaseAuthUser ->
                            Log.d(TAG, "Firebase Auth successful for: ${firebaseAuthUser.email}")

                            // Launch another coroutine for Firestore operation
                            viewModelScope.launch(Dispatchers.IO) {
                                try {
                                    firebaseUserRepository.getFirebaseUser(
                                        firebaseAuthUser.email.toString(),
                                        onSuccess = { myFirebaseUser ->
                                            viewModelScope.launch(Dispatchers.Main) {
                                                if (myFirebaseUser == null) {
                                                    Log.e(TAG, "User not found in Firestore database")
                                                    _uiState.value = _uiState.value.copy(
                                                        authState = AuthState.Error("User profile not found. Please contact support.")
                                                    )
                                                } else {
                                                    Log.d(TAG, "User retrieved successfully: ${myFirebaseUser.userId}")

                                                    // Save login state and update UI
                                                    saveLoginState(myFirebaseUser)

                                                    _uiState.value = _uiState.value.copy(
                                                        authState = AuthState.Authenticated(myFirebaseUser)
                                                    )
                                                }
                                            }
                                        },
                                        onFailure = { exception ->
                                            Log.e(TAG, "Failed to retrieve user from Firestore", exception)
                                            viewModelScope.launch(Dispatchers.Main) {
                                                _uiState.value = _uiState.value.copy(
                                                    authState = AuthState.Error(getReadableErrorMessage(exception))
                                                )
                                            }
                                        }
                                    )
                                } catch (e: Exception) {
                                    Log.e(TAG, "Error during Firestore user retrieval", e)
                                    viewModelScope.launch(Dispatchers.Main) {
                                        _uiState.value = _uiState.value.copy(
                                            authState = AuthState.Error("Failed to load user profile: ${e.message}")
                                        )
                                    }
                                }
                            }
                        },
                        onFailure = { exception ->
                            Log.e(TAG, "Firebase Authentication failed", exception)
                            viewModelScope.launch(Dispatchers.Main) {
                                _uiState.value = _uiState.value.copy(
                                    authState = AuthState.Error(getReadableErrorMessage(exception))
                                )
                            }
                        }
                    )
                }

            } catch (exception: Exception) {
                Log.e(TAG, "Login failed with unexpected exception", exception)
                _uiState.value = _uiState.value.copy(
                    authState = AuthState.Error(getReadableErrorMessage(exception))
                )
            }
        }
    }

    // Helper function to save login state
    private suspend fun saveLoginState(user: MyFirebaseUser) {
        try {
            val currentTime = System.currentTimeMillis()

            // Update user's last login time in Firestore
            firebaseUserRepository.updateUserLastLogin(user.userId, currentTime)

            // Save to local preferences
            loginPreference.saveLoginState(
                isLoggedIn = true,
                userId = user.userId,
                username = user.name,
                email = user.email,
                lastLoginTime = currentTime
            )

            // Update local Room database if needed
            val roomUser = ModelMappers.mapToRoomUser(user.copy(lastLoginAt = currentTime))
            roomUserRepository.insertUser(roomUser)

            Log.d(TAG, "Login state saved successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving login state", e)
            // Don't fail the login process for this
        }
    }

    // Helper function to provide user-friendly error messages
    private fun getReadableErrorMessage(exception: Exception): String {
        return when {
            exception.message?.contains("invalid-email", ignoreCase = true) == true ->
                "Please enter a valid email address"
            exception.message?.contains("user-disabled", ignoreCase = true) == true ->
                "This account has been disabled. Please contact support."
            exception.message?.contains("user-not-found", ignoreCase = true) == true ->
                "No account found with this email address"
            exception.message?.contains("wrong-password", ignoreCase = true) == true ->
                "Incorrect password. Please try again."
            exception.message?.contains("too-many-requests", ignoreCase = true) == true ->
                "Too many failed attempts. Please try again later."
            exception.message?.contains("network", ignoreCase = true) == true ->
                "Network error. Please check your connection."
            exception.message?.contains("timeout", ignoreCase = true) == true ->
                "Request timed out. Please try again."
            else -> exception.message ?: "Login failed. Please try again."
        }
    }

    fun clearForm() {
        _uiState.value = _uiState.value.copy(
            email = "",
            password = "",
            confirmPassword = "",
            formErrors = FormErrors(),
            authState = AuthState.Idle
        )
    }

    fun clearError() {
        if (_uiState.value.authState is AuthState.Error) {
            _uiState.value = _uiState.value.copy(authState = AuthState.Idle)
        }
    }

    fun logout() {
        firebaseUserRepository.signOut()
        _uiState.value = AuthUiState(authState = AuthState.Unauthenticated)
    }

    fun isLoggedIn(): Boolean {
        return loginPreference.isLoggedIn
    }
}