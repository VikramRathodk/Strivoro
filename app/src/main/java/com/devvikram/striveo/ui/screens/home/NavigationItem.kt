package com.devvikram.striveo.ui.screens.home

import androidx.compose.ui.graphics.vector.ImageVector

 data class NavigationItem(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String
)