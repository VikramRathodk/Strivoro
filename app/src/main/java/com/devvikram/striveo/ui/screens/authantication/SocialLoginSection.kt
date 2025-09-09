package com.devvikram.striveo.ui.screens.authantication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Games
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.devvikram.striveo.ui.reuseables.buttons.Buttons

@Composable
fun SocialLoginSection(
    onGoogleSignUp: () -> Unit,
    onFacebookSignUp: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Or continue with",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Buttons.SecondaryButton(
                modifier = Modifier.weight(1f),
                text = "Google",
                onClick = onGoogleSignUp,
                icon = Icons.Filled.Games
            )

            Buttons.SecondaryButton(
                modifier = Modifier.weight(1f),
                text = "Facebook",
                onClick = onFacebookSignUp,

                )
        }
    }
}