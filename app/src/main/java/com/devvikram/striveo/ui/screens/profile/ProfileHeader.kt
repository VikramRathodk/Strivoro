package com.devvikram.striveo.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import coil3.compose.AsyncImage
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devvikram.striveo.config.constants.AppThemeMode
import com.devvikram.striveo.config.constants.PreferenceLanguage
import com.devvikram.striveo.room.model.RoomUser
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileHeader(
    user: RoomUser,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box {
            // Gradient background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Profile Picture with border
                Box(
                    modifier = Modifier.padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (user.avatarUrl.isNullOrEmpty()) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = "Profile Picture",
                                modifier = Modifier.size(50.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else {
                        AsyncImage(
                            model = user.avatarUrl,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Verification badge
                    if (user.isEmailVerified) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(32.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            shadowElevation = 4.dp
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Verified,
                                contentDescription = "Verified",
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(20.dp),
                                tint = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Name with better typography
                Text(
                    text = user.name.ifEmpty { "No name provided" },
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Account Type with chip design
                Surface(
                    modifier = Modifier.padding(top = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
                ) {
                    Text(
                        text = user.userType.name.replace("_", " ").lowercase().replaceFirstChar {
                            it.titlecase()
                        },
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ProfileDetails(
    user: RoomUser,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ContactPage,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Contact Information",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            ProfileDetailItem(
                icon = Icons.Outlined.Email,
                label = "Email Address",
                value = user.email.ifEmpty { "Not provided" },
                isVerified = user.isEmailVerified
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )

            ProfileDetailItem(
                icon = Icons.Outlined.Phone,
                label = "Phone Number",
                value = user.phone.ifEmpty { "Not provided" }
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )

            ProfileDetailItem(
                icon = Icons.Outlined.Badge,
                label = "User ID",
                value = user.userId
            )

            user.lastLoginAt?.let { timestamp ->
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
                val date = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
                    .format(Date(timestamp))
                ProfileDetailItem(
                    icon = Icons.Outlined.AccessTime,
                    label = "Last Login",
                    value = date
                )
            }
        }
    }
}

@Composable
private fun ProfileDetailItem(
    icon: ImageVector,
    label: String,
    value: String,
    isVerified: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier
                    .padding(8.dp)
                    .size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = 2.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (isVerified) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Filled.Verified,
                        contentDescription = "Verified",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

// Sealed class to represent different types of preferences
sealed class PreferenceType {
    data class Switch(
        val isChecked: Boolean,
        val onCheckedChange: (Boolean) -> Unit
    ) : PreferenceType()

    data class Dropdown(
        val currentValue: String,
        val options: List<String>,
        val onValueChange: (String) -> Unit
    ) : PreferenceType()

    data class ReadOnly(
        val value: String
    ) : PreferenceType()

    // Future preference types can be added here
    data class Slider(
        val value: Float,
        val range: ClosedFloatingPointRange<Float>,
        val onValueChange: (Float) -> Unit
    ) : PreferenceType()

    data class Text(
        val value: String,
        val onValueChange: (String) -> Unit
    ) : PreferenceType()
}

// Data class to represent a preference item
data class PreferenceItem(
    val icon: ImageVector,
    val label: String,
    val description: String? = null,
    val type: PreferenceType
)

@Composable
fun ProfileSettings(
    user: RoomUser,
    modifier: Modifier = Modifier,
    onDarkModeChange: (AppThemeMode) -> Unit = {},
    onNotificationsChange: (Boolean) -> Unit = {},
    onLanguageChange: (PreferenceLanguage) -> Unit = {},
) {
    val availableLanguages = PreferenceLanguage.entries

    val preferences = listOf(
        PreferenceItem(
            icon = Icons.Outlined.Language,
            label = "Language",
            description = "Choose your preferred language",
            type = PreferenceType.Dropdown(
                currentValue = user.preferredLanguage.displayName,
                options = availableLanguages.map { it.displayName },
                onValueChange = { selectedDisplayName ->
                    val selectedLanguage = availableLanguages.find {
                        it.displayName == selectedDisplayName
                    } ?: PreferenceLanguage.ENGLISH
                    onLanguageChange(selectedLanguage)
                }
            )
        ),
        PreferenceItem(
            icon = if (user.appThemeMode.name == "DARK") Icons.Outlined.DarkMode
            else Icons.Outlined.LightMode,
            label = "Dark Mode",
            description = "Toggle dark theme",
            type = PreferenceType.Switch(
                isChecked = user.appThemeMode.name == "DARK",
                onCheckedChange = {
                    onDarkModeChange(if (it) AppThemeMode.DARK else AppThemeMode.LIGHT)
                }
            )
        ),
        PreferenceItem(
            icon = if (user.allowNotifications) Icons.Filled.Notifications
            else Icons.Outlined.NotificationsOff,
            label = "Push Notifications",
            description = if (user.allowNotifications) "Enabled" else "Disabled",
            type = PreferenceType.Switch(
                isChecked = user.allowNotifications,
                onCheckedChange = onNotificationsChange
            )
        )
        // Future preferences can be easily added here
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Preferences",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Render preference items
            preferences.forEachIndexed { index, preference ->
                PreferenceItemComposable(preference = preference)

                // Add divider between items (except for the last item)
                if (index < preferences.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PreferenceItemComposable(
    preference: PreferenceItem,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side: Icon and label
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
            ) {
                Icon(
                    imageVector = preference.icon,
                    contentDescription = preference.label,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(24.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = preference.label,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                preference.description?.let { description ->
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Right side: Control based on preference type
        when (val type = preference.type) {
            is PreferenceType.Switch -> {
                Switch(
                    checked = type.isChecked,
                    onCheckedChange = type.onCheckedChange
                )
            }

            is PreferenceType.Dropdown -> {
                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                        modifier = Modifier
                            .menuAnchor()
                            .clickable { expanded = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = type.currentValue,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = if (expanded) Icons.Filled.ExpandLess
                                else Icons.Filled.ExpandMore,
                                contentDescription = "Dropdown",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        type.options.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = option,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                },
                                onClick = {
                                    type.onValueChange(option)
                                    expanded = false
                                },
                                leadingIcon = if (option == type.currentValue) {
                                    {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = "Selected",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                } else null
                            )
                        }
                    }
                }
            }

            is PreferenceType.ReadOnly -> {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = type.value,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            is PreferenceType.Slider -> {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${type.value.toInt()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Slider(
                        value = type.value,
                        onValueChange = type.onValueChange,
                        valueRange = type.range,
                        modifier = Modifier.width(120.dp)
                    )
                }
            }

            is PreferenceType.Text -> {
                var isEditing by remember { mutableStateOf(false) }
                var textValue by remember { mutableStateOf(type.value) }

                if (isEditing) {
                    OutlinedTextField(
                        value = textValue,
                        onValueChange = { textValue = it },
                        modifier = Modifier.width(120.dp),
                        textStyle = MaterialTheme.typography.bodySmall,
                        singleLine = true,
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    type.onValueChange(textValue)
                                    isEditing = false
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Save",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    )
                } else {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                        modifier = Modifier.clickable { isEditing = true }
                    ) {
                        Text(
                            text = type.value,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}
