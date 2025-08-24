package com.devvikram.striveo.ui.screens.tasks

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devvikram.striveo.config.enums.TaskPriority

@Composable
 fun PrioritySelector(
    selectedPriority: TaskPriority,
    onPrioritySelected: (TaskPriority) -> Unit
) {
    Column {
        Text(
            text = "Priority",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(TaskPriority.values()) { priority ->
                val isSelected = selectedPriority == priority
                val (color, bgColor) = when (priority) {
                    TaskPriority.HIGH -> Color(0xFFFF5252) to Color(0xFFFFEBEE)
                    TaskPriority.MEDIUM -> Color(0xFFFF9800) to Color(0xFFFFF3E0)
                    TaskPriority.LOW -> Color(0xFF4CAF50) to Color(0xFFE8F5E8)
                }

                Card(
                    modifier = Modifier
                        .clickable { onPrioritySelected(priority) }
                        .animateContentSize(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) bgColor else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) color else MaterialTheme.colorScheme.outline.copy(
                            alpha = 0.3f
                        )
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(color, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = priority.name.lowercase().replaceFirstChar { it.uppercase() },
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) color else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}