package com.devvikram.striveo.ui.reuseables.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

// Dialog State Management
@Stable
class DialogState {
    var isVisible by mutableStateOf(false)
        private set

    fun show() {
        isVisible = true
    }

    fun hide() {
        isVisible = false
    }
}

@Composable
fun rememberDialogState(): DialogState {
    return remember { DialogState() }
}

// Dialog Configuration Data Classes
data class DialogConfig(
    val title: String,
    val message: String,
    val icon: ImageVector? = null,
    val confirmText: String = "OK",
    val dismissText: String? = "Cancel",
    val onConfirm: () -> Unit = {},
    val onDismiss: () -> Unit = {},
    val dismissOnClickOutside: Boolean = true,
    val type: DialogType = DialogType.INFO
)

enum class DialogType {
    INFO, SUCCESS, WARNING, ERROR, CONFIRMATION
}

// Main Reusable Dialog Component
@Composable
fun ReusableDialog(
    state: DialogState,
    config: DialogConfig,
    modifier: Modifier = Modifier
) {
    if (state.isVisible) {
        Dialog(
            onDismissRequest = {
                if (config.dismissOnClickOutside) {
                    state.hide()
                    config.onDismiss()
                }
            }
        ) {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Icon (if provided)
                    config.icon?.let { icon ->
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier
                                .size(48.dp)
                                .padding(bottom = 16.dp),
                            tint = getDialogColor(config.type)
                        )
                    }

                    // Title
                    Text(
                        text = config.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Message
                    Text(
                        text = config.message,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 24.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (config.dismissText != null)
                            Arrangement.spacedBy(8.dp, Alignment.End)
                        else
                            Arrangement.Center
                    ) {
                        // Dismiss button (if provided)
                        config.dismissText?.let { dismissText ->
                            TextButton(
                                onClick = {
                                    state.hide()
                                    config.onDismiss()
                                }
                            ) {
                                Text(dismissText)
                            }
                        }

                        // Confirm button
                        Button(
                            onClick = {
                                state.hide()
                                config.onConfirm()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = getDialogColor(config.type)
                            )
                        ) {
                            Text(config.confirmText)
                        }
                    }
                }
            }
        }
    }
}

// Helper function to get color based on dialog type
@Composable
private fun getDialogColor(type: DialogType) = when (type) {
    DialogType.SUCCESS -> MaterialTheme.colorScheme.primary
    DialogType.WARNING -> MaterialTheme.colorScheme.tertiary
    DialogType.ERROR -> MaterialTheme.colorScheme.error
    DialogType.CONFIRMATION -> MaterialTheme.colorScheme.secondary
    DialogType.INFO -> MaterialTheme.colorScheme.primary
}

// Convenient Dialog Builders
object DialogBuilder {

    fun info(
        title: String,
        message: String,
        onConfirm: () -> Unit = {},
        onDismiss: () -> Unit = {},
        confirmText: String = "OK",
        dismissText: String? = null
    ) = DialogConfig(
        title = title,
        message = message,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        confirmText = confirmText,
        dismissText = dismissText,
        type = DialogType.INFO
    )

    fun success(
        title: String,
        message: String,
        onConfirm: () -> Unit = {},
        onDismiss: () -> Unit = {},
        confirmText: String = "Great!"
    ) = DialogConfig(
        title = title,
        message = message,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        confirmText = confirmText,
        type = DialogType.SUCCESS
    )

    fun error(
        title: String,
        message: String,
        onConfirm: () -> Unit = {},
        onDismiss: () -> Unit = {},
        confirmText: String = "OK"
    ) = DialogConfig(
        title = title,
        message = message,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        confirmText = confirmText,
        type = DialogType.ERROR
    )

    fun warning(
        title: String,
        message: String,
        onConfirm: () -> Unit = {},
        onDismiss: () -> Unit = {},
        confirmText: String = "Understood",
        dismissText: String = "Cancel"
    ) = DialogConfig(
        title = title,
        message = message,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        confirmText = confirmText,
        dismissText = dismissText,
        type = DialogType.WARNING
    )

    fun confirmation(
        title: String,
        message: String,
        onConfirm: () -> Unit,
        onDismiss: () -> Unit = {},
        confirmText: String = "Confirm",
        dismissText: String = "Cancel"
    ) = DialogConfig(
        title = title,
        message = message,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        confirmText = confirmText,
        dismissText = dismissText,
        type = DialogType.CONFIRMATION
    )
}

// Usage Example in a Composable
@Composable
fun ExampleUsage() {
    // Create dialog states
    val infoDialogState = rememberDialogState()
    val confirmDialogState = rememberDialogState()
    val errorDialogState = rememberDialogState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Trigger buttons
        Button(onClick = { infoDialogState.show() }) {
            Text("Show Info Dialog")
        }

        Button(onClick = { confirmDialogState.show() }) {
            Text("Show Confirmation Dialog")
        }

        Button(onClick = { errorDialogState.show() }) {
            Text("Show Error Dialog")
        }
    }

    // Dialog instances
    ReusableDialog(
        state = infoDialogState,
        config = DialogBuilder.info(
            title = "Information",
            message = "This is an informational message to help you understand something important."
        )
    )

    ReusableDialog(
        state = confirmDialogState,
        config = DialogBuilder.confirmation(
            title = "Delete Item",
            message = "Are you sure you want to delete this item? This action cannot be undone.",
            onConfirm = {
                // Handle deletion
                println("Item deleted")
            }
        )
    )

    ReusableDialog(
        state = errorDialogState,
        config = DialogBuilder.error(
            title = "Error",
            message = "Something went wrong while processing your request. Please try again."
        )
    )
}

// Custom Dialog Extension
@Composable
fun ReusableDialog(
    state: DialogState,
    title: String,
    message: String,
    confirmText: String = "OK",
    dismissText: String? = null,
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {},
    type: DialogType = DialogType.INFO,
    icon: ImageVector? = null
) {
    ReusableDialog(
        state = state,
        config = DialogConfig(
            title = title,
            message = message,
            confirmText = confirmText,
            dismissText = dismissText,
            onConfirm = onConfirm,
            onDismiss = onDismiss,
            type = type,
            icon = icon
        )
    )
}