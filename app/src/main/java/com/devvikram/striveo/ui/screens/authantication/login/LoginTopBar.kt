package com.devvikram.striveo.ui.screens.authantication.login

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.devvikram.striveo.ui.reuseables.ArrowBackIcon
import com.devvikram.striveo.ui.reuseables.toolbar.ReusableToolbar
import com.devvikram.striveo.ui.screens.authantication.AuthenticationViewModel

@Composable
fun LoginTopBar(
    loginState: AuthenticationViewModel.LoginState,
    onBackToRegister: (() -> Unit)? = null
) {
    val (title, backgroundColor, navigationIcon) = when (loginState) {
        is AuthenticationViewModel.LoginState.Initial ->
            Triple(
                "Sign In",
                MaterialTheme.colorScheme.primary,
                onBackToRegister?.let { ArrowBackIcon(it) }
            )

        is AuthenticationViewModel.LoginState.Error ->
            Triple(
                loginState.message,
                MaterialTheme.colorScheme.error,
                onBackToRegister?.let { ArrowBackIcon(it) }
            )

        AuthenticationViewModel.LoginState.Loading ->
            Triple(
                "Signing In...",
                MaterialTheme.colorScheme.primary,
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp)
                )
            )

        is AuthenticationViewModel.LoginState.Success ->
            Triple(
                loginState.message,
                MaterialTheme.colorScheme.primary,
                onBackToRegister?.let { ArrowBackIcon(it) }
            )
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
                style = when (loginState) {
                    is AuthenticationViewModel.LoginState.Initial -> MaterialTheme.typography.titleMedium
                    else -> MaterialTheme.typography.titleSmall
                },
                color = when (loginState) {
                    is AuthenticationViewModel.LoginState.Initial -> MaterialTheme.colorScheme.onPrimary
                    else -> LocalContentColor.current
                }
            )
        },
        navigationIcon = { navigationIcon },
        onNavigationClick = if (loginState is AuthenticationViewModel.LoginState.Loading) null else onBackToRegister
    )
}