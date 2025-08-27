package com.devvikram.striveo.ui.main

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.devvikram.striveo.ui.navigation.Destination
import com.devvikram.striveo.ui.screens.authantication.AuthenticationViewModel
import com.devvikram.striveo.ui.screens.authantication.login.LoginScreen
import com.devvikram.striveo.ui.screens.authantication.register.RegistrationScreen
import com.devvikram.striveo.ui.screens.home.HomeScreen
import com.devvikram.striveo.ui.screens.onboarding.OnBoardingScreen
import com.devvikram.striveo.ui.screens.onboarding.OnboardingViewModel
import com.devvikram.striveo.ui.screens.profile.ProfileScreen

@Composable
fun AppScreen(
    appViewModel: AppViewmodel
) {

    val navController = rememberNavController()
    val isLoggedIn by appViewModel.loginState.collectAsState()
    val isOnboardingComplete by appViewModel.onboardingState.collectAsState()
    val authenticationViewModel: AuthenticationViewModel = hiltViewModel()

//    val isDarkMode by appViewModel.darkModeState.collectAsState()


    Log.d("AppScreen", "isLoggedIn=$isLoggedIn, isOnboardingComplete=$isOnboardingComplete")

    val startDestination = when {
        !isOnboardingComplete -> Destination.Onboarding.route
        !isLoggedIn -> Destination.Login.route
        else -> Destination.Home.route
    }

    // Navigate to appropriate screen when login state changes
    LaunchedEffect(isLoggedIn, isOnboardingComplete) {
        if (isOnboardingComplete) {
            if (isLoggedIn) {
                navController.navigate(Destination.Home.route) {
                    popUpTo(0) { inclusive = true }
                }
            } else {
                navController.navigate(Destination.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }


    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Destination.Onboarding.route) {
            val onboardingViewModel: OnboardingViewModel = hiltViewModel()

            OnBoardingScreen(
                viewModel = onboardingViewModel,
                onOnboardingComplete = {
                    appViewModel.setOnboardingComplete()
                    navController.navigate(Destination.Login.route) {
                        popUpTo(Destination.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Destination.Login.route) {
            LoginScreen(
                authenticationViewModel = authenticationViewModel,
                onLoginSuccess = {
                    navController.navigate(Destination.Home.route) {
                        popUpTo(0)
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Destination.Register.route)
                },
            )
        }

        composable(Destination.Register.route) {
            RegistrationScreen(
                authenticationViewModel = authenticationViewModel,
                onRegisterSuccess = {
                    navController.navigate(Destination.Login.route) {
                        popUpTo(0)
                    }
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Destination.Home.route) {
            HomeScreen(
                mainNavController = navController,
                onLogout = {
                    appViewModel.logout()
                }
            )
        }
        composable(Destination.Profile.route) {
            ProfileScreen(
                onBackPressed = {
                    navController.popBackStack()
                },
                onLogout = {
                    appViewModel.logout()
                },
            )
        }
    }
}

