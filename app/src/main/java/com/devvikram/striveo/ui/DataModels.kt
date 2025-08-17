package com.devvikram.striveo.ui


import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector


// Data classes
data class TaskStats(
    val totalTasks: Int,
    val completedTasks: Int,
    val pendingTasks: Int,
    val overdueTasks: Int,
    val focusTime: String,
    val productivity: Int,
    val weeklyCompletion: Int
)

data class QuickAction(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val color: Color,
    val onClick: () -> Unit
)