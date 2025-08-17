package com.devvikram.striveo.ui.screens.authantication

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devvikram.striveo.R
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class FloatingIcon(
    val icon: ImageVector,
    val initialX: Float,
    val initialY: Float,
    val speed: Float,
    val scale: Float,
    val rotationSpeed: Float
)

@Composable
fun AnimatedProductivityBackground() {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val infiniteTransition = rememberInfiniteTransition(label = "background_animation")

    val animationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "animation_progress"
    )

    val floatingIcons = remember {
        listOf(
            FloatingIcon(Icons.Default.DateRange, 0.1f, 0.2f, 0.5f, 0.6f, 1f),
            FloatingIcon(Icons.Default.CheckCircle, 0.8f, 0.1f, 0.3f, 0.4f, -0.8f),
            FloatingIcon(Icons.Default.Star, 0.2f, 0.7f, 0.4f, 0.5f, 1.2f),
            FloatingIcon(Icons.Default.CheckCircle, 0.5f, 0.15f, 0.5f, 0.5f, 0.7f),
            FloatingIcon(Icons.Default.Star, 0.95f, 0.4f, 0.3f, 0.6f, -0.6f),
        )
    }
    val backgroundColor = MaterialTheme.colorScheme.background
    val primaryColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    val secondaryColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .blur(1.dp)
    ) {
        // Draw gradient circles using theme colors
        val centerX = size.width / 2
        val centerY = size.height / 2

        // Animated gradient circles
        val circle1Offset = Offset(
            (centerX + cos(animationProgress * 2 * Math.PI) * 100f).toFloat(),
            (centerY + sin(animationProgress * 2 * Math.PI) * 80f).toFloat()
        )

        val circle2Offset = Offset(
            (centerX - cos(animationProgress * 1.5 * Math.PI) * 120f).toFloat(),
            (centerY - sin(animationProgress * 1.5 * Math.PI) * 60f).toFloat()
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    primaryColor,
                    Color.Transparent
                ),
                radius = 200f
            ),
            radius = 150f,
            center = circle1Offset
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    secondaryColor,
                    Color.Transparent
                ),
                radius = 180f
            ),
            radius = 120f,
            center = circle2Offset
        )

        // Draw floating particles using primary color
        repeat(15) { index ->
            val particleProgress = (animationProgress + index * 0.1f) % 1f
            val x = (index * 127f) % size.width
            val y = size.height * (1f - particleProgress)

            drawCircle(
                color = primaryColor,
                radius = 2f + (index % 3) * 1f,
                center = Offset(x, y)
            )
        }
    }

    // Floating icons using theme primary color
    floatingIcons.forEach { floatingIcon ->
        val iconX by infiniteTransition.animateFloat(
            initialValue = floatingIcon.initialX,
            targetValue = floatingIcon.initialX + 0.1f,
            animationSpec = infiniteRepeatable(
                animation = tween((8000 / floatingIcon.speed).toInt(), easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "icon_x_${floatingIcon.icon}"
        )

        val iconY by infiniteTransition.animateFloat(
            initialValue = floatingIcon.initialY,
            targetValue = floatingIcon.initialY + 0.15f,
            animationSpec = infiniteRepeatable(
                animation = tween((10000 / floatingIcon.speed).toInt(), easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "icon_y_${floatingIcon.icon}"
        )

        val iconRotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween((15000 / floatingIcon.rotationSpeed).toInt(), easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "icon_rotation_${floatingIcon.icon}"
        )

        Icon(
            imageVector = floatingIcon.icon,
            contentDescription = null,
            modifier = Modifier
                .offset(
                    x = screenWidth * iconX,
                    y = screenHeight * iconY
                )
                .scale(floatingIcon.scale)
                .rotate(iconRotation)
                .alpha(0.15f),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun LoginScreen(
    uiState: AuthUiState,
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onLoginClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onClearError: () -> Unit = {},
    onNavigateToMain: () -> Unit = {}
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    // Handle authentication state changes
    LaunchedEffect(uiState.authState) {
        when (uiState.authState) {
            is AuthState.Authenticated -> {
                onNavigateToMain()
            }
            else -> { /* Handle other states in UI */ }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = Color.Transparent
            )
    ) {
        // Animated background
        AnimatedProductivityBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Welcome Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Welcome to Striveo",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "Your productivity companion",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }

            // Login Form Card with glassmorphism effect
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()

                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp)
                    ) {
                        // Email Field
                        OutlinedTextField(
                            value = uiState.email,
                            onValueChange = {
                                onEmailChange(it)
                                if (uiState.authState is AuthState.Error) {
                                    onClearError()
                                }
                            },
                            label = {
                                Text(
                                    "Email Address",
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Email",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            isError = uiState.formErrors.emailError != null,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                errorBorderColor = MaterialTheme.colorScheme.error,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                cursorColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        )

                        // Email Error
                        AnimatedVisibility(
                            visible = uiState.formErrors.emailError != null,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            uiState.formErrors.emailError?.let { error ->
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Password Field
                        OutlinedTextField(
                            value = uiState.password,
                            onValueChange = {
                                onPasswordChange(it)
                                if (uiState.authState is AuthState.Error) {
                                    onClearError()
                                }
                            },
                            label = {
                                Text(
                                    "Password",
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Password",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        painter = painterResource(if (passwordVisible) R.drawable.visibility_on_icon else R.drawable.visility_off_icon),
                                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            isError = uiState.formErrors.passwordError != null,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { onLoginClick() }
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                errorBorderColor = MaterialTheme.colorScheme.error,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                cursorColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        )

                        // Password Error
                        AnimatedVisibility(
                            visible = uiState.formErrors.passwordError != null,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            uiState.formErrors.passwordError?.let { error ->
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Login Button with gradient using theme colors
                        Button(
                            onClick = onLoginClick,
                            enabled = !uiState.isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 8.dp,
                                pressedElevation = 4.dp
                            )
                        ) {
                            when (uiState.authState) {
                                is AuthState.Loading -> {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            strokeWidth = 2.dp
                                        )
                                        Text(
                                            "Signing In...",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                }
                                else -> {
                                    Text(
                                        "Sign In",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Forgot Password Link
                        TextButton(
                            onClick = onForgotPasswordClick,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Forgot Password?",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Register Section
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Don't have an account?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    TextButton(onClick = onRegisterClick) {
                        Text(
                            "Sign Up",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // General Error Message
            AnimatedVisibility(
                visible = uiState.authState is AuthState.Error,
                enter = expandVertically(
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                ) + fadeIn(),
                exit = shrinkVertically(
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                ) + fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = uiState.errorMessage ?: "An error occurred",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}