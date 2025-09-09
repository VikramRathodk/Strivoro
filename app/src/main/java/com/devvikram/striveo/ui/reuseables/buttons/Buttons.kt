package com.devvikram.striveo.ui.reuseables.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object Buttons {

    // 1. Primary Button - Main CTA buttons
    @Composable
    fun PrimaryButton(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        isLoading: Boolean = false,
        icon: ImageVector? = null
    ) {
        Button(
            onClick = onClick,
            enabled = enabled && !isLoading,
            modifier = modifier.height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    // 2. Secondary Button - Alternative actions
    @Composable
    fun SecondaryButton(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        icon: ImageVector? = null
    ) {
        OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier.height(48.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    // 3. Text Button - Minimal emphasis
    @Composable
    fun TextButton(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        icon: ImageVector? = null
    ) {
        TextButton(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    // 4. Danger/Destructive Button
    @Composable
    fun DangerButton(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        isLoading: Boolean = false
    ) {
        Button(
            onClick = onClick,
            enabled = enabled && !isLoading,
            modifier = modifier.height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = MaterialTheme.colorScheme.onError,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = text,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onError
                )
            }
        }
    }

    // 5. Icon Button - For toolbar actions
    @Composable
    fun IconButton(
        icon: ImageVector,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        contentDescription: String? = null,
        tint: Color = MaterialTheme.colorScheme.onSurface
    ) {
        IconButton(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint
            )
        }
    }

    // 6. Floating Action Button
    @Composable
    fun FloatingActionButton(
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        icon: ImageVector = Icons.Default.Add,
        contentDescription: String? = null
    ) {
        FloatingActionButton(
            onClick = onClick,
            modifier = modifier,
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }

    // 7. Chip Button - For filters/tags
    @Composable
    fun ChipButton(
        text: String,
        isSelected: Boolean,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true
    ) {
        FilterChip(
            onClick = onClick,
            label = {
                Text(
                    text = text,
                    fontSize = 14.sp
                )
            },
            selected = isSelected,
            enabled = enabled,
            modifier = modifier,
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
            )
        )
    }

    // 8. Toggle Button
    @Composable
    fun ToggleButton(
        text: String,
        isChecked: Boolean,
        onToggle: (Boolean) -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true
    ) {
        OutlinedButton(
            onClick = { onToggle(!isChecked) },
            enabled = enabled,
            modifier = modifier.height(40.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (isChecked) MaterialTheme.colorScheme.primary else Color.Transparent,
                contentColor = if (isChecked) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
            ),
            border = BorderStroke(
                1.dp,
                if (isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            )
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }

    // 9. Social Login Buttons
    @Composable
    fun SocialButton(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        icon: ImageVector,
        backgroundColor: Color = MaterialTheme.colorScheme.surface,
        contentColor: Color = MaterialTheme.colorScheme.onSurface
    ) {
        OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier.height(48.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = backgroundColor,
                contentColor = contentColor
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }

    // 10. Small Button - Compact version
    @Composable
    fun SmallButton(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true
    ) {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier.height(32.dp),
            colors = ButtonDefaults.buttonColors(),
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
        ) {
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ButtonsPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Buttons.PrimaryButton(
                text = "Primary Button",
                onClick = { },
                icon = Icons.Default.Check
            )

            Buttons.SecondaryButton(
                text = "Secondary Button",
                onClick = { },
                icon = Icons.Default.Edit
            )

            Buttons.TextButton(
                text = "Text Button",
                onClick = { }
            )

            Buttons.DangerButton(
                text = "Delete",
                onClick = { }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Buttons.ChipButton(
                    text = "Selected",
                    isSelected = true,
                    onClick = { }
                )
                Buttons.ChipButton(
                    text = "Not Selected",
                    isSelected = false,
                    onClick = { }
                )
            }

            Buttons.ToggleButton(
                text = "Toggle Me",
                isChecked = false,
                onToggle = { }
            )

            Buttons.SocialButton(
                text = "Continue with Google",
                onClick = { },
                icon = Icons.Default.AccountCircle
            )

            Buttons.SmallButton(
                text = "Small",
                onClick = { }
            )
        }
    }
}