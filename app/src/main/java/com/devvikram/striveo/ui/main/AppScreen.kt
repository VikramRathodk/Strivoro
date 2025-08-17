package com.devvikram.striveo.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.devvikram.striveo.ui.navigation.Destination
import com.devvikram.striveo.ui.screens.authantication.AuthenticationViewModel
import com.devvikram.striveo.ui.screens.authantication.LoginScreen
import com.devvikram.striveo.ui.screens.authantication.RegistrationScreen
import com.devvikram.striveo.ui.screens.home.HomeScreen
import com.devvikram.striveo.ui.screens.onboarding.OnBoardingScreen
import com.devvikram.striveo.ui.screens.onboarding.OnboardingViewModel

@Composable
fun AppScreen(
    appViewModel: AppViewmodel
){
    val navController = rememberNavController()

    val authViewModel: AuthenticationViewModel = hiltViewModel()
    val onboardingViewModel: OnboardingViewModel = hiltViewModel()
//    val startDestination = if (onboardingViewModel.isOnboardingCompleted()) {
//        if (authViewModel.isLoggedIn()) Destination.Home else Destination.Login
//    } else {
//        Destination.Onboarding
//    }

    NavHost(
        navController = navController,
        startDestination = Destination.Home.route
    ) {

        composable(Destination.Onboarding.route) {
            val onboardingViewModel: OnboardingViewModel = hiltViewModel()

            OnBoardingScreen (
                viewModel = onboardingViewModel,
                onOnboardingComplete = {
                    navController.navigate(Destination.Login.route){
                        popUpTo(Destination.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Destination.Login.route) {
            val authenticationViewModel: AuthenticationViewModel = hiltViewModel()
            val uiState by authenticationViewModel.uiState.collectAsState()

            if (uiState.isAuthenticated) {
                navController.navigate(Destination.Home.route)
            }

            LoginScreen(
                uiState = uiState,
                onEmailChange = { authenticationViewModel.updateEmail(it) },
                onPasswordChange = { authenticationViewModel.updatePassword(it) },
                onLoginClick = { authenticationViewModel.login() },
                onRegisterClick = { navController.navigate(Destination.Register.route) },
                onForgotPasswordClick = { /* Handle forgot password */ }
            )
        }
        composable(Destination.Register.route) {
            RegistrationScreen()
        }
        composable(Destination.Home.route) {
            HomeScreen(
                mainNavController = navController,
                onLogout = {
                    appViewModel.logout()
                }
            )

        }
    }

}