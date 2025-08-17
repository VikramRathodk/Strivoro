package com.devvikram.striveo.ui.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.outlined.AccessAlarm
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Task
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.devvikram.striveo.ui.navigation.Destination

@Composable
fun NavigationBar(
    currentRoute: String?,
    homeNavController: NavController
) {
    val navigationItems = listOf(
        NavigationItem(
            route = Destination.Home.route,
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            label = "Home"
        ),
        NavigationItem(
            route = Destination.Calendar.route,
            selectedIcon = Icons.Filled.CalendarToday,
            unselectedIcon = Icons.Outlined.CalendarToday,
            label = "Calendar"
        ),
        NavigationItem(
            route = Destination.Reminders.route,
            selectedIcon = Icons.Filled.Alarm,
            unselectedIcon = Icons.Outlined.AccessAlarm,
            label = "Reminders"
        ),
        NavigationItem(
            route = Destination.TaskCreation.route,
            selectedIcon = Icons.Filled.Task,
            unselectedIcon = Icons.Outlined.Task,
            label = "Tasks"
        )
    )
    val ambientColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        tonalElevation = 12.dp,
        modifier = Modifier
            .shadow(
                elevation = 8.dp,
                ambientColor = ambientColor
            )
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        navigationItems.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                icon = {
                    AnimatedContent(
                        targetState = isSelected,
                        transitionSpec = {
                            scaleIn(tween(200)) togetherWith scaleOut(tween(200))
                        },
                        label = "icon_animation"
                    ) { selected ->
                        Icon(
                            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.label,
                            modifier = Modifier.size(if (selected) 26.dp else 24.dp)
                        )
                    }
                },
                label = {
                    Text(
                        text = item.label,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        fontSize = if (isSelected) 12.sp else 11.sp
                    )
                },
                selected = isSelected,
                onClick = {
                    homeNavController.navigate(item.route) {
                        popUpTo(homeNavController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            )
        }
    }
}