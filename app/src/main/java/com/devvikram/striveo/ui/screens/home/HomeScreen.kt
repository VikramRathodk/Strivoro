package com.devvikram.striveo.ui.screens.home

import android.os.Build
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.devvikram.striveo.ui.navigation.Destination
import com.devvikram.striveo.ui.screens.calender.CalenderScreen
import com.devvikram.striveo.ui.screens.reminders.RemindersScreen
import com.devvikram.striveo.ui.screens.tasks.TaskCreationScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    mainNavController: NavController,
    onLogout: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val homeNavController = rememberNavController()
    val navBackStackEntry by homeNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // scroll behavior for collapsible toolbar
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    // Animation for FAB
    val fabVisible by remember {
        derivedStateOf { currentRoute == Destination.Home.route }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            if (currentRoute == Destination.Home.route) {
                HomeToolbar(
                    greeting = viewModel.greeting,
                    onProfileClick = { /* Handle profile click */ },
                    onNotificationClick = { /* Handle notification click */ },
                    scrollBehavior = scrollBehavior
                )
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = fabVisible,
                enter = scaleIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                AiSuggestionsButton(
                    onClick = { /* Handle AI suggestions */ }
                )
            }
        },
        bottomBar = {
            NavigationBar(
                currentRoute = currentRoute,
                homeNavController = homeNavController
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = homeNavController,
            startDestination = Destination.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Destination.Home.route) {
                HomeDashboardScreen(
                    viewModel = viewModel,
                    onTaskClick = { taskId ->
                        mainNavController.navigate(Destination.TaskDetails.createRoute(taskId))
                    }
                )
            }
            composable(Destination.Calendar.route) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    CalenderScreen(
                        onAddTask = {}
                    )
                }
            }
            composable(Destination.Reminders.route) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    RemindersScreen()
                }
            }
            composable(Destination.TaskCreation.route) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    TaskCreationScreen()
                }
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        mainNavController = NavController(androidx.compose.ui.platform.LocalContext.current),
        onLogout = {},
    )
}