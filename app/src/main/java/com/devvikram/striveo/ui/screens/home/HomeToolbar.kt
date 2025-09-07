package com.devvikram.striveo.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.devvikram.striveo.ui.reuseables.chips.TagChip
import com.devvikram.striveo.ui.reuseables.dialogs.DialogBuilder
import com.devvikram.striveo.ui.reuseables.dialogs.ReusableDialog
import com.devvikram.striveo.ui.reuseables.dialogs.rememberDialogState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeToolbar(
    streakCount: Int = 10,
    greeting: String,
    onProfileClick: () -> Unit,
    onNotificationClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    onLogout: () -> Unit = {},
    name: String = "User",
    onProjectClick: () -> Unit
) {
    val isLogoutConfirmation = remember { mutableStateOf(false) }
    val confirmDialogState = rememberDialogState()

    LargeTopAppBar(
        title = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // greeting section with gradient text effect
                AnimatedVisibility(
                    visible = scrollBehavior.state.collapsedFraction < 0.5f,
                    enter = fadeIn(tween(300)) + slideInVertically(tween(300)),
                    exit = fadeOut(tween(200)) + slideOutVertically(tween(200))
                ) {
                    Column(
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .animateContentSize()
                    ) {
                        Row {
                            Text(
                                text = "${greeting}," ,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = name,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.5.sp,
                                modifier = Modifier.padding(start = 4.dp),
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }


                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "What's on your mind?",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            letterSpacing = (-0.5).sp,
                            modifier = Modifier.animateContentSize(),
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        },
        actions = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 16.dp)
            ) {

                TagChip(
                    tag = "PROJECTS",
                    onClick = {
                        onProjectClick()
                    },
                    isSelected = true,
                    isIconVisible = false
                )

                // Streak Counter
                StreakCounter(
                    streakCount = streakCount,
                    modifier = Modifier.animateContentSize()
                )

//                IconButton(
//                    onClick = {
//                        isLogoutConfirmation.value = true
//                    }
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.ExitToApp,
//                        contentDescription = "Logout",
//                    )
//                }

                // Profile Avatar
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = CircleShape,
                            ambientColor = Color(0xFF667EEA).copy(alpha = 0.3f)
                        )
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF667EEA),
                                    Color(0xFF764BA2),
                                    Color(0xFF6B73FF)
                                )
                            ),
                            shape = CircleShape
                        )
                        .clickable { onProfileClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        name.take(1).uppercase(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            scrolledContainerColor = MaterialTheme.colorScheme.primary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )

    if(isLogoutConfirmation.value){
        confirmDialogState.show()
    }
    ReusableDialog(
        state = confirmDialogState,
        config = DialogBuilder.confirmation(
            title = "Logout",
            message = "Are you sure you want to logout?",
            onConfirm = {
                onLogout()
                isLogoutConfirmation.value = false
            },
            onDismiss = {
                isLogoutConfirmation.value = false
            }
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun HomeToolbarPreview() {
    HomeToolbar(
        greeting = "Good Morning",
        onProfileClick = {},
        onNotificationClick = {},
        scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(),
        onLogout = {},
        onProjectClick = {}
    )
}