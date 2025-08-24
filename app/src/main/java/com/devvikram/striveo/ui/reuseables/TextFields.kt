package com.devvikram.striveo.ui.reuseables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object TextFields {

    /**
     * Common/General purpose text field for all standard input types
     */
    @Composable
    fun CommonTextField(
        value: String,
        onValueChange: (String) -> Unit,
        label: String,
        placeholder: String = "",
        leadingIcon: ImageVector? = null,
        trailingIcon: ImageVector? = null,
        onTrailingIconClick: (() -> Unit)? = null,
        isRequired: Boolean = false,
        isError: Boolean = false,
        minLines: Int = 1,
        maxLines: Int = 1,
        keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
        keyboardActions: KeyboardActions = KeyboardActions.Default,
        visualTransformation: VisualTransformation = VisualTransformation.None,
        enabled: Boolean = true,
        readOnly: Boolean = false,
        errorMessage: String? = null,
        helperText: String? = null,
        maxCharacters: Int? = null,
        showCharacterCount: Boolean = false,
        modifier: Modifier = Modifier
    ) {
        // Character count validation
        val isCharacterLimitExceeded = maxCharacters?.let { value.length > it } ?: false
        val actualIsError = isError || isCharacterLimitExceeded

        Column(modifier = modifier) {
            // Label with required indicator
            Text(
                text = buildString {
                    append(label)
                    if (isRequired) append(" *")
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = when {
                    !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    actualIsError -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier.padding(bottom = 8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            OutlinedTextField(
                value = value,
                onValueChange = { newValue ->
                    // Apply character limit if specified
                    val finalValue = if (maxCharacters != null && newValue.length > maxCharacters) {
                        newValue.take(maxCharacters)
                    } else {
                        newValue
                    }
                    onValueChange(finalValue)
                },
                placeholder = {
                    Text(
                        text = placeholder,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = leadingIcon?.let { icon ->
                    {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = when {
                                !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                actualIsError -> MaterialTheme.colorScheme.error
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                },
                trailingIcon = trailingIcon?.let { icon ->
                    {
                        if (onTrailingIconClick != null) {
                            IconButton(
                                onClick = onTrailingIconClick,
                                enabled = enabled
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (!enabled) MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = 0.38f
                                    ) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (!enabled) MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.38f
                                ) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                visualTransformation = visualTransformation,
                singleLine = maxLines == 1,
                minLines = minLines,
                maxLines = maxLines,
                isError = actualIsError,
                enabled = enabled,
                readOnly = readOnly,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (actualIsError) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = if (actualIsError) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    focusedTextColor = if (!enabled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    else MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = if (!enabled) MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.38f
                    ) else MaterialTheme.colorScheme.onSurface
                )
            )

            // Error message, helper text, and character count
            val supportingText = when {
                actualIsError && errorMessage != null -> errorMessage
                !actualIsError && helperText != null -> helperText
                else -> null
            }

            if (supportingText != null || (showCharacterCount && maxCharacters != null)) {
                Column(modifier = Modifier.padding(top = 4.dp)) {
                    // Supporting text (error or helper)
                    if (supportingText != null) {
                        Text(
                            text = supportingText,
                            fontSize = 12.sp,
                            color = if (actualIsError) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .alpha(if (enabled) 1f else 0.6f)
                        )
                    }

                    // Character count
                    if (showCharacterCount && maxCharacters != null) {
                        Text(
                            text = "${value.length}/$maxCharacters",
                            fontSize = 12.sp,
                            color = when {
                                isCharacterLimitExceeded -> MaterialTheme.colorScheme.error
                                !enabled -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = if (supportingText != null) 2.dp else 0.dp)
                        )
                    }
                }
            }
        }
    }

    /**
     * Dedicated password text field with visibility toggle
     */
    @Composable
    fun PasswordTextField(
        value: String,
        onValueChange: (String) -> Unit,
        label: String,
        placeholder: String = "Enter password",
        leadingIcon: ImageVector? = null,
        isRequired: Boolean = false,
        isError: Boolean = false,
        keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        keyboardActions: KeyboardActions = KeyboardActions.Default,
        enabled: Boolean = true,
        errorMessage: String? = null,
        helperText: String? = null,
        maxCharacters: Int? = null,
        showCharacterCount: Boolean = false,
        modifier: Modifier = Modifier
    ) {
        var passwordVisible by remember { mutableStateOf(false) }

        // Character count validation
        val isCharacterLimitExceeded = maxCharacters?.let { value.length > it } ?: false
        val actualIsError = isError || isCharacterLimitExceeded

        Column(modifier = modifier) {
            // Label with required indicator
            Text(
                text = buildString {
                    append(label)
                    if (isRequired) append(" *")
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = when {
                    !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    actualIsError -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier.padding(bottom = 8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            OutlinedTextField(
                value = value,
                onValueChange = { newValue ->
                    // Apply character limit if specified
                    val finalValue = if (maxCharacters != null && newValue.length > maxCharacters) {
                        newValue.take(maxCharacters)
                    } else {
                        newValue
                    }
                    onValueChange(finalValue)
                },
                placeholder = {
                    Text(
                        text = placeholder,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = leadingIcon?.let { icon ->
                    {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = when {
                                !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                actualIsError -> MaterialTheme.colorScheme.error
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                },
                trailingIcon = {
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible },
                        enabled = enabled
                    ) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = if (!enabled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                isError = actualIsError,
                enabled = enabled,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (actualIsError) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = if (actualIsError) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    focusedTextColor = if (!enabled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    else MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = if (!enabled) MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.38f
                    ) else MaterialTheme.colorScheme.onSurface
                )
            )

            // Error message, helper text, and character count
            val supportingText = when {
                actualIsError && errorMessage != null -> errorMessage
                !actualIsError && helperText != null -> helperText
                else -> null
            }

            if (supportingText != null || (showCharacterCount && maxCharacters != null)) {
                Column(modifier = Modifier.padding(top = 4.dp)) {
                    // Supporting text (error or helper)
                    if (supportingText != null) {
                        Text(
                            text = supportingText,
                            fontSize = 12.sp,
                            color = if (actualIsError) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .alpha(if (enabled) 1f else 0.6f)
                        )
                    }

                    // Character count
                    if (showCharacterCount && maxCharacters != null) {
                        Text(
                            text = "${value.length}/$maxCharacters",
                            fontSize = 12.sp,
                            color = when {
                                isCharacterLimitExceeded -> MaterialTheme.colorScheme.error
                                !enabled -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = if (supportingText != null) 2.dp else 0.dp)
                        )
                    }
                }
            }
        }
    }


    /**
     * Multiline text field for longer text input
     */
    @Composable
    fun MultilineTextField(
        value: String,
        onValueChange: (String) -> Unit,
        label: String,
        placeholder: String = "",
        leadingIcon: ImageVector? = null,
        trailingIcon: ImageVector? = null,
        onTrailingIconClick: (() -> Unit)? = null,
        isRequired: Boolean = false,
        isError: Boolean = false,
        minLines: Int = 3,
        maxLines: Int = 5,
        keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
        keyboardActions: KeyboardActions = KeyboardActions.Default,
        enabled: Boolean = true,
        errorMessage: String? = null,
        helperText: String? = null,
        maxCharacters: Int? = null,
        showCharacterCount: Boolean = false,
        modifier: Modifier = Modifier
    ) {
        CommonTextField(
            value = value,
            onValueChange = onValueChange,
            label = label,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            onTrailingIconClick = onTrailingIconClick,
            isRequired = isRequired,
            isError = isError,
            minLines = minLines,
            maxLines = maxLines,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            enabled = enabled,
            errorMessage = errorMessage,
            helperText = helperText,
            maxCharacters = maxCharacters,
            showCharacterCount = showCharacterCount,
            modifier = modifier
        )
    }

    /**
     * Email text field with appropriate keyboard type
     */
    @Composable
    fun EmailTextField(
        modifier: Modifier = Modifier,
        value: String,
        onValueChange: (String) -> Unit,
        label: String = "Email",
        placeholder: String = "Enter your email",
        leadingIcon: ImageVector? = null,
        trailingIcon: ImageVector? = null,
        onTrailingIconClick: (() -> Unit)? = null,
        isRequired: Boolean = false,
        isError: Boolean = false,
        keyboardActions: KeyboardActions = KeyboardActions.Default,
        enabled: Boolean = true,
        errorMessage: String? = null,
        helperText: String? = null,
    ) {
        CommonTextField(
            value = value,
            onValueChange = onValueChange,
            label = label,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            onTrailingIconClick = onTrailingIconClick,
            isRequired = isRequired,
            isError = isError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            keyboardActions = keyboardActions,
            enabled = enabled,
            errorMessage = errorMessage,
            helperText = helperText,
            modifier = modifier
        )
    }

    /**
     * Number text field for numeric input
     */
    @Composable
    fun NumberTextField(
        value: String,
        onValueChange: (String) -> Unit,
        label: String,
        placeholder: String = "",
        leadingIcon: ImageVector? = null,
        trailingIcon: ImageVector? = null,
        onTrailingIconClick: (() -> Unit)? = null,
        isRequired: Boolean = false,
        isError: Boolean = false,
        keyboardActions: KeyboardActions = KeyboardActions.Default,
        enabled: Boolean = true,
        errorMessage: String? = null,
        helperText: String? = null,
        modifier: Modifier = Modifier
    ) {
        CommonTextField(
            value = value,
            onValueChange = onValueChange,
            label = label,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            onTrailingIconClick = onTrailingIconClick,
            isRequired = isRequired,
            isError = isError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            keyboardActions = keyboardActions,
            enabled = enabled,
            errorMessage = errorMessage,
            helperText = helperText,
            modifier = modifier
        )
    }
}