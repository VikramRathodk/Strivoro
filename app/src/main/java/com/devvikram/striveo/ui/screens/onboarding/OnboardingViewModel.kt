package com.devvikram.striveo.ui.screens.onboarding

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devvikram.striveo.AppUtils.Companion.generateStrongPassword
import com.devvikram.striveo.config.constants.App
import com.devvikram.striveo.config.constants.LoginPreference
import com.devvikram.striveo.config.mappers.ModelMappers
import com.devvikram.striveo.firebase.repository.FirebaseUserRepository
import com.devvikram.striveo.room.model.RoomUser
import com.devvikram.striveo.room.repository.RoomUserRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
open class OnboardingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    val loginPreference: LoginPreference,
    private val firebaseUserRepository: FirebaseUserRepository,
    private val roomUserRepository: RoomUserRepository,
    private val firebaseFirestore: FirebaseFirestore
) : ViewModel() {

    private val TAG = "OnboardingViewModel"

    fun isOnboardingCompleted(): Boolean {
        return loginPreference.isOnboardingCompleted()
    }

    fun completeOnboarding() {
        loginPreference.setOnboardingCompleted(true)
    }

}