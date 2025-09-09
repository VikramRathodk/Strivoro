package com.devvikram.striveo.ui.screens.authantication.register

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PasswordRequirementsCard(
    modifier: Modifier = Modifier,
    password: String = ""
) {

    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Password Requirements:",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))

            val requirements = listOf(
                "At least 8 characters" to (password.length >= 8),
                "One uppercase letter" to password.any { it.isUpperCase() },
                "One lowercase letter" to password.any { it.isLowerCase() },
                "One number" to password.any { it.isDigit() }
            )

            requirements.forEach { (requirement, met) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (met) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                        contentDescription = if (met) "Requirement met" else "Requirement not met",
                        tint = if (met) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        },
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = requirement,
                        fontSize = 13.sp,
                        fontWeight = if (met) FontWeight.Medium else FontWeight.Normal,
                        color = if (met) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PasswordRequirementsCardPreview() {
    MaterialTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            // Preview with empty password
            PasswordRequirementsCard(password = "")
            Spacer(modifier = Modifier.height(16.dp))
            // Preview with valid password
            PasswordRequirementsCard(password = "Test23")
        }
    }
}