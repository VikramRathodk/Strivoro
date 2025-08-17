package com.devvikram.striveo.ui.navigation

sealed class Destination(val route: String) {

    object Splash : Destination("splash")
    object Onboarding : Destination("onboarding")
    object Login : Destination("login")
    object Register : Destination("register")
    object Home : Destination("home")

    object TaskCreation : Destination("taskCreation")

    object TaskDetails : Destination("taskDetails/{taskId}") {
        fun createRoute(taskId: String) = "taskDetails/$taskId"
    }

    object Calendar : Destination("calendar")

    object Reminders : Destination("remainders")

    object Notes : Destination("notes")

    object Profile : Destination("profile")

}