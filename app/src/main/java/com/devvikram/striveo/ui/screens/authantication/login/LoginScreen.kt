package com.devvikram.striveo.ui.screens.authantication.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devvikram.striveo.ui.reuseables.TextFields
import com.devvikram.striveo.ui.reuseables.buttons.Buttons
import com.devvikram.striveo.ui.screens.authantication.AuthenticationViewModel
import com.devvikram.striveo.ui.screens.authantication.SocialLoginSection
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onForgotPassword: () -> Unit = {},
    authenticationViewModel: AuthenticationViewModel
) {

    val email by authenticationViewModel.loginEmail.collectAsState()
    val password by authenticationViewModel.loginPassword.collectAsState()
    val loginState by authenticationViewModel.loginState.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            authenticationViewModel.resetLoginState()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            LoginTopBar(
                loginState = loginState,
                onBackToRegister = onNavigateToRegister
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.Start
            ) {

                // Welcome Text Section
                item {
                    WelcomeSection()
                }

                // Email
                item {
                    TextFields.EmailTextField(
                        value = email,
                        onValueChange = { authenticationViewModel.updateLoginEmail(it) },
                        label = "Email Address",
                        placeholder = "Enter your email",
                        leadingIcon = Icons.Default.Email,
                        isError = email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches(),
                        errorMessage = if (email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) "Please enter a valid email" else null
                    )
                }

                // Password
                item {
                    TextFields.PasswordTextField(
                        value = password,
                        onValueChange = { authenticationViewModel.updateLoginPassword(it) },
                        label = "Password",
                        placeholder = "Enter your password",
                        leadingIcon = Icons.Default.Lock,
                        isError = false,
                        errorMessage = null
                    )
                }

                // Forgot Password Link
                item {
                    ForgotPasswordLink(
                        modifier = Modifier.fillMaxWidth(),
                        onForgotPassword = onForgotPassword
                    )
                }

                // Login Error Message (if any)
                item {
                    LoginErrorMessage(
                        loginState = loginState
                    )
                }

                // Sign In button
                item {
                    val isFormValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                            password.isNotEmpty()

                    Buttons.PrimaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = when (loginState) {
                            is AuthenticationViewModel.LoginState.Loading -> "Signing In..."
                            else -> "Sign In"
                        },
                        onClick = {
                            authenticationViewModel.loginUser(onLoginSuccess)
                        },
                        enabled = isFormValid && loginState !is AuthenticationViewModel.LoginState.Loading
                    )
                }

                // Register Link
                item {
                    val annotatedString = buildAnnotatedString {
                        append("Don't have an account? ")
                        pushStringAnnotation(
                            tag = "register",
                            annotation = "register"
                        )
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        ) {
                            append("Create Account")
                        }
                        pop()
                    }

                    ClickableText(
                        text = annotatedString,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            textAlign = TextAlign.Center
                        ),
                        onClick = { offset ->
                            annotatedString.getStringAnnotations(
                                tag = "register",
                                start = offset,
                                end = offset
                            ).firstOrNull()?.let {
                                onNavigateToRegister()
                            }
                        }
                    )
                }
            }
        }
    }
}

private fun getTimeBasedLoginGreeting(): String {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5..11 -> "Good Morning!"
        in 12..16 -> "Good Afternoon!"
        in 17..24 -> "Good Evening!"
        else -> "Welcome Back!"
    }
}
@Composable
private fun WelcomeSection() {
    val greeting = getTimeBasedLoginGreeting()
    val subtitle = "Please sign in to get started"

    var greetingText by remember { mutableStateOf("") }
    var subtitleText by remember { mutableStateOf("") }
    var showCursor by remember { mutableStateOf(false) }

    LaunchedEffect(greeting) {
        while (true) {
            // Clear texts
            greetingText = ""
            subtitleText = ""
            showCursor = true
            delay(500)

            // Animate greeting
            greeting.forEachIndexed { index, _ ->
                greetingText = greeting.substring(0, index + 1)
                delay(50)
            }
            delay(200)

            // Animate subtitle
            subtitle.forEachIndexed { index, _ ->
                subtitleText = subtitle.substring(0, index + 1)
                delay(30)
            }

            // Blink cursor for a while
            repeat(6) {
                showCursor = !showCursor
                delay(500)
            }

            delay(1000) // Hold before repeating
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = greetingText + if (showCursor && greetingText.length < greeting.length) "|" else "",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = subtitleText + if (showCursor && subtitleText.length < subtitle.length && greetingText.length >= greeting.length) "|" else "",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun ForgotPasswordLink(
    modifier: Modifier = Modifier,
    onForgotPassword: () -> Unit
) {
    val annotatedString = buildAnnotatedString {
        pushStringAnnotation(
            tag = "forgot",
            annotation = "forgot"
        )
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        ) {
            append("Forgot Password?")
        }
        pop()
    }

    ClickableText(
        text = annotatedString,
        modifier = modifier.padding(vertical = 4.dp),
        style = MaterialTheme.typography.bodyMedium.copy(
            textAlign = TextAlign.End
        ),
        onClick = { offset ->
            annotatedString.getStringAnnotations(
                tag = "forgot",
                start = offset,
                end = offset
            ).firstOrNull()?.let {
                onForgotPassword()
            }
        }
    )
}

@Composable
private fun LoginErrorMessage(
    loginState: AuthenticationViewModel.LoginState
) {
    if (loginState is AuthenticationViewModel.LoginState.Error) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
            ),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ErrorOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = loginState.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}


@Preview
@Composable
fun LoginScreenPreview() {
    // Preview implementation
}