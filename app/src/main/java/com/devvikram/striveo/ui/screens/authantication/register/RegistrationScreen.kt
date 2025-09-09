package com.devvikram.striveo.ui.screens.authantication.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person3
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devvikram.striveo.ui.reuseables.TextFields
import com.devvikram.striveo.ui.reuseables.buttons.Buttons
import com.devvikram.striveo.ui.screens.authantication.AuthenticationViewModel

@Composable
fun RegistrationScreen(
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit,
    onNavigateToTerms: () -> Unit = {},
    onNavigateToPrivacy: () -> Unit = {},
    authenticationViewModel: AuthenticationViewModel
) {

    val name by authenticationViewModel.registrationName.collectAsState()
    val email by authenticationViewModel.registrationEmail.collectAsState()
    val password by authenticationViewModel.registrationPassword.collectAsState()
    val phoneNumber by authenticationViewModel.registrationPhoneNumber.collectAsState()
    val signUpState by authenticationViewModel.signUpState.collectAsState()
    val isTermsAccepted by authenticationViewModel.isTermsAccepted.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            authenticationViewModel.resetRegistrationState()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            RegistrationTopBar(
                signUpState = signUpState,
                onBackToLogin = onBackToLogin
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

                // Name
                item {
                    TextFields.CommonTextField(
                        value = name,
                        onValueChange = { authenticationViewModel.updateRegistrationName(it) },
                        label = "Full Name",
                        placeholder = "Enter your full name",
                        leadingIcon = Icons.Default.Person3,
                        isRequired = true,
                        isError = name.isNotEmpty() && name.length < 2,
                        errorMessage = if (name.isNotEmpty() && name.length < 2) "Name must be at least 2 characters" else null
                    )
                }

                // Email
                item {
                    TextFields.EmailTextField(
                        value = email,
                        onValueChange = { authenticationViewModel.updateRegistrationEmail(it) },
                        label = "Email Address",
                        placeholder = "Enter your email",
                        leadingIcon = Icons.Default.Email,
                        isError = email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches(),
                        errorMessage = if (email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) "Please enter a valid email" else null
                    )
                }

                // Phone Number (Optional)
                item {
                    TextFields.NumberTextField(
                        value = phoneNumber,
                        onValueChange = { authenticationViewModel.updateRegistrationPhoneNumber(it) },
                        label = "Phone Number (Optional)",
                        placeholder = "Enter your phone number",
                        leadingIcon = Icons.Default.Phone,
                    )
                }

                // Password
                item {
                    TextFields.PasswordTextField(
                        value = password,
                        onValueChange = { authenticationViewModel.updateRegistrationPassword(it) },
                        label = "Password",
                        placeholder = "Create a strong password",
                        leadingIcon = Icons.Default.Lock,
                        isError = password.isNotEmpty() && password.length < 8,
                        errorMessage = if (password.isNotEmpty() && password.length < 8) "Password must be at least 8 characters" else null
                    )
                }

                // Password Requirements
                item {
                    PasswordRequirementsCard(
                        password = password
                    )
                }

                // Terms and Conditions
                item {
                    TermsAndConditionsCheckbox(
                        modifier = Modifier.fillMaxWidth(),
                        isChecked = isTermsAccepted,
                        onCheckedChange = { authenticationViewModel.updateTermsAcceptance(it) },
                        onNavigateToTerms = onNavigateToTerms,
                        onNavigateToPrivacy = onNavigateToPrivacy
                    )
                }


                // Sign Up button
                item {
                    val isFormValid = name.length >= 2 &&
                            android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                            password.length >= 8 &&
                            isTermsAccepted

                    Buttons.PrimaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = when (signUpState) {
                            is AuthenticationViewModel.SignUpState.Loading -> "Creating Account..."
                            else -> "Create Account"
                        },
                        onClick = {
                            authenticationViewModel.registerUser(onRegisterSuccess)
                        },
                        enabled = isFormValid && signUpState !is AuthenticationViewModel.SignUpState.Loading
                    )
                }

                // Login Link
                item {
                    val annotatedString = buildAnnotatedString {
                        append("Already have an account? ")
                        pushStringAnnotation(
                            tag = "login",
                            annotation = "login"
                        )
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        ) {
                            append("Sign In")
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
                                tag = "login",
                                start = offset,
                                end = offset
                            ).firstOrNull()?.let {
                                onBackToLogin()
                            }
                        }
                    )
                }
            }
        }
    }
}



@Preview
@Composable
fun RegistrationScreenPreview() {
    // Preview implementation
}