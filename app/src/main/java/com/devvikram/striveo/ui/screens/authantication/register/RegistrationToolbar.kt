package com.devvikram.striveo.ui.screens.authantication.register

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.devvikram.striveo.ui.reuseables.ArrowBackIcon
import com.devvikram.striveo.ui.reuseables.toolbar.ReusableToolbar
import com.devvikram.striveo.ui.screens.authantication.AuthenticationViewModel

@Composable
fun RegistrationTopBar(
    signUpState: AuthenticationViewModel.SignUpState,
    onBackToLogin: () -> Unit
) {
    val (title, backgroundColor, navigationIcon) = when (signUpState) {
        is AuthenticationViewModel.SignUpState.Initial ->
            Triple(
                "Create Your Account",
                MaterialTheme.colorScheme.primary,

                ArrowBackIcon(onBackToLogin)
            )

        is AuthenticationViewModel.SignUpState.Error ->
            Triple(signUpState.message, MaterialTheme.colorScheme.error, ArrowBackIcon(onBackToLogin))

        AuthenticationViewModel.SignUpState.Loading ->
            Triple("Loading...", MaterialTheme.colorScheme.primary, CircularProgressIndicator(
                modifier = Modifier.size(32.dp)
            ))

        is AuthenticationViewModel.SignUpState.Success ->
            Triple(signUpState.message, MaterialTheme.colorScheme.primary, ArrowBackIcon(onBackToLogin))
    }

    ReusableToolbar(
        backgroundColor = backgroundColor,
        title = {
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                softWrap = false,
                style = when (signUpState) {
                    is AuthenticationViewModel.SignUpState.Initial -> MaterialTheme.typography.titleMedium
                    else -> MaterialTheme.typography.titleSmall
                },
                color = when (signUpState) {
                    is AuthenticationViewModel.SignUpState.Initial -> MaterialTheme.colorScheme.onPrimary
                    else -> LocalContentColor.current
                }
            )
        },
        navigationIcon = { navigationIcon },
        onNavigationClick = if (signUpState is AuthenticationViewModel.SignUpState.Loading) null else onBackToLogin
    )
}