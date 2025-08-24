package com.devvikram.striveo.ui.screens.authantication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devvikram.striveo.config.constants.App
import com.devvikram.striveo.config.constants.LoginPreference
import com.devvikram.striveo.config.mappers.ModelMappers
import com.devvikram.striveo.firebase.repository.FirebaseUserRepository
import com.devvikram.striveo.room.model.RoomUser
import com.devvikram.striveo.room.repository.RoomUserRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val loginPreference: LoginPreference,
    private val firebaseUserRepository: FirebaseUserRepository,
    private val roomUserRepository: RoomUserRepository,
    private val firebaseFirestore: FirebaseFirestore
) : ViewModel() {


    // Sign up

    private val _registrationName = MutableStateFlow("")
    val registrationName : StateFlow<String> = _registrationName.asStateFlow()

    private val _registrationEmail = MutableStateFlow("")
    val registrationEmail : StateFlow<String> = _registrationEmail.asStateFlow()


    private val _registrationPassword = MutableStateFlow("")
    val registrationPassword : StateFlow<String> = _registrationPassword.asStateFlow()

    private val _registrationPhoneNumber = MutableStateFlow("")
    val registrationPhoneNumber : StateFlow<String> = _registrationPhoneNumber.asStateFlow()


    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Initial)
    val signUpState: StateFlow<SignUpState> = _signUpState.asStateFlow()


    private val isTermAccepted = MutableStateFlow(false)
    val isTermsAccepted: StateFlow<Boolean> = isTermAccepted.asStateFlow()


    fun updateRegistrationName(name: String) {
        _registrationName.value = name
    }

    fun updateRegistrationEmail(email: String) {
        _registrationEmail.value = email
    }


    fun updateRegistrationPassword(password: String) {
        _registrationPassword.value = password
    }
    fun updateTermsAcceptance(isAccepted: Boolean) {
        isTermAccepted.value = isAccepted
    }
    fun updateRegistrationPhoneNumber(phoneNumber: String) {
        _registrationPhoneNumber.value = phoneNumber
    }

    fun registerUser(onRegisterSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                _signUpState.value = SignUpState.Loading

                val userId = firebaseFirestore.collection(App.FIREBASE_COLLECTION_USERS).document().id
                
                val roomUser = RoomUser(
                    userId =userId,
                    name = _registrationName.value,
                    phone = _registrationPhoneNumber.value,
                    email = _registrationEmail.value,
                    password = _registrationPassword.value,
                    lastLoginAt = System.currentTimeMillis(),
                    lastActiveAt = System.currentTimeMillis(),
                    platform ="android",
                    createdAt  =  System.currentTimeMillis(),
                    lastModifiedAt = System.currentTimeMillis(),
                )
                
                roomUserRepository.insertUser(roomUser)
                firebaseUserRepository.insertUserToFirebase(
                    user = ModelMappers.mapToFirebaseUser(roomUser),
                    onSuccess = {
                        _signUpState.value = SignUpState.Success("User registered successfully")
                    },
                    onFailure = { exception ->
                        _signUpState.value = SignUpState.Error("Registration failed: ${exception.message}")
                    }
                )
            }catch (e: Exception){
                _signUpState.value = SignUpState.Error("Registration failed: ${e.message}")
            }

        }
        onRegisterSuccess()
    }

    fun resetRegistrationState(){
        _signUpState.value = SignUpState.Initial
    }

    // sign up state

    sealed class SignUpState {
        object Initial : SignUpState()
        object Loading : SignUpState()
        data class Success(
            val message: String
        ) : SignUpState()
        data class Error(val message: String) : SignUpState()
    }


    // Sign In


    private val _loginEmail = MutableStateFlow("")
    val loginEmail : StateFlow<String> = _loginEmail.asStateFlow()

    private val _loginPassword = MutableStateFlow("")
    val loginPassword : StateFlow<String> = _loginPassword.asStateFlow()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun updateLoginEmail(email: String){
        _loginEmail.value = email
    }

    fun updateLoginPassword(password: String){
        _loginPassword.value = password
    }
    fun resetLoginState(){
        _loginState.value = LoginState.Initial
    }

    fun loginUser(
        onLoginSuccess: () -> Unit
    ){
        viewModelScope.launch {
            try {
                _loginState.value = LoginState.Loading

                firebaseUserRepository.signIn(
                    email = _loginEmail.value,
                    password = _loginPassword.value,
                    onSuccess = {
                        _loginState.value = LoginState.Success("Login successful")
                        loginPreference.saveLoginState(
                            isLoggedIn = true,
                            userId = it.userId,
                            username = it.name,
                            email = it.email,
                            authToken = it.password,
                            rememberMe = true,
                            lastLoginTime = System.currentTimeMillis()
                        )
                        onLoginSuccess()
                    },
                    onFailure = { exception ->
                        _loginState.value = LoginState.Error("Login failed: ${exception.message}")
                    }
                )
            }catch (e: Exception){
                _loginState.value = LoginState.Error("Login failed: ${e.message}")
            }
        }
    }



    sealed class LoginState {
        object Initial : LoginState()
        object Loading : LoginState()
        data class Success(
            val message: String
        ) : LoginState()
        data class Error(val message: String) : LoginState()
    }

}