package com.devvikram.striveo.ui.screens.authantication.register

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

@Composable
fun TermsAndConditionsCheckbox(
    modifier: Modifier = Modifier,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onNavigateToTerms: () -> Unit,
    onNavigateToPrivacy: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(end = 8.dp)
        )

        val annotatedString = buildAnnotatedString {
            append("I agree to the ")
            pushStringAnnotation(tag = "terms", annotation = "terms")
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            ) {
                append("Terms of Service")
            }
            pop()
            append(" and ")
            pushStringAnnotation(tag = "privacy", annotation = "privacy")
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            ) {
                append("Privacy Policy")
            }
            pop()
        }

        ClickableText(
            text = annotatedString,
            style = MaterialTheme.typography.bodySmall,
            onClick = { offset ->
                annotatedString.getStringAnnotations(tag = "terms", start = offset, end = offset)
                    .firstOrNull()?.let { onNavigateToTerms() }
                annotatedString.getStringAnnotations(tag = "privacy", start = offset, end = offset)
                    .firstOrNull()?.let { onNavigateToPrivacy() }
            }
        )
    }
}