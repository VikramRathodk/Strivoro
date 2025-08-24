package com.devvikram.striveo.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import coil3.compose.AsyncImage
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
fun ProfileSettings(
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

            ProfileSettingItem(
                icon = Icons.Outlined.Language,
                label = "Language",
                value = user.preferredLanguage.name.replace("_", " ").lowercase()
                    .replaceFirstChar { it.titlecase() }
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )

            ProfileSettingItem(
                icon = Icons.Outlined.DarkMode,
                label = "Theme",
                value = user.appThemeMode.name.replace("_", " ").lowercase()
                    .replaceFirstChar { it.titlecase() }
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )

            // Enhanced notification toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (user.allowNotifications) Icons.Filled.Notifications
                        else Icons.Outlined.NotificationsOff,
                        contentDescription = "Notifications",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Push Notifications",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (user.allowNotifications) "Enabled" else "Disabled",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Switch(
                    checked = user.allowNotifications,
                    onCheckedChange = { /* Handle toggle */ },
                    enabled = false // Make it read-only for now
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

@Composable
 fun ProfileSettingItem(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(24.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}