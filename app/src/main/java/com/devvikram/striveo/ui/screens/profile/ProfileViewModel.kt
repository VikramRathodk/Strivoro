package com.devvikram.striveo.ui.screens.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devvikram.striveo.config.constants.LoginPreference
import com.devvikram.striveo.room.model.RoomUser
import com.devvikram.striveo.room.repository.RoomUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val roomUserRepository: RoomUserRepository,
    private val loginPreference: LoginPreference
)  : ViewModel()
{

    private val _userProfileState = MutableStateFlow<ProfileState?>(ProfileState.Initial)
    val userProfileState: StateFlow<ProfileState?> = _userProfileState.asStateFlow()

    private val _logoutConfirmationState = MutableStateFlow(false)
    val logoutConfirmationState: StateFlow<Boolean> = _logoutConfirmationState.asStateFlow()


    init {
        val userId = loginPreference.getUserId()
        if (userId.isNotEmpty()) {
            fetchUserProfile(userId)
        }
    }

    fun updateLogoutConfirmationState(newState: Boolean) {
        _logoutConfirmationState.value = newState
    }

    private fun fetchUserProfile(userId: String) {
        viewModelScope.launch {
            _userProfileState.value = ProfileState.Loading
            try {
                val user = roomUserRepository.getUserById(userId)
                _userProfileState.value = ProfileState.Success(user)

            } catch (e: Exception) {
                _userProfileState.value = ProfileState.Error(e.message ?: "Unknown error")
            }
        }
    }

    sealed class ProfileState {
        object Initial : ProfileState()
        object Loading : ProfileState()
        data class Success(val user: RoomUser?) : ProfileState()
        data class Error(val message: String) : ProfileState()

    }



}