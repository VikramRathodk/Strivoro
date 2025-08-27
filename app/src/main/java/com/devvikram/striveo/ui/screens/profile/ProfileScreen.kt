package com.devvikram.striveo.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devvikram.striveo.ui.reuseables.ArrowBackIcon
import com.devvikram.striveo.ui.reuseables.appversion.AppVersionInfo
import com.devvikram.striveo.ui.reuseables.dialogs.DialogBuilder
import com.devvikram.striveo.ui.reuseables.dialogs.ReusableDialog
import com.devvikram.striveo.ui.reuseables.dialogs.rememberDialogState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
    onLogout: () -> Unit,
) {
    val userProfileState by viewModel.userProfileState.collectAsState()
    val logoutConfirmationState by viewModel.logoutConfirmationState.collectAsState()
    val confirmDialogState = rememberDialogState()


    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    ArrowBackIcon {
                        onBackPressed()
                    }
                },
                title = { Text("Profile") },
                actions = {
                    // logout
                    IconButton(onClick = {
                        viewModel.updateLogoutConfirmationState(true)
                    }) {
                        Icon(
                            Icons.Default.ExitToApp,
                            contentDescription = "Logout",

                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        when (userProfileState) {
            is ProfileViewModel.ProfileState.Initial -> {
                // Initial state - could show empty state or loading
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Loading profile...")
                }
            }

            is ProfileViewModel.ProfileState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ProfileViewModel.ProfileState.Success -> {
                val user = (userProfileState as ProfileViewModel.ProfileState.Success).user
                if (user != null) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = paddingValues,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp)
                            )
                        }
                        item {
                            ProfileHeader(
                                user = user,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }

                        item {
                            ProfileDetails(
                                user = user,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }

                        item {
                            ProfileSettings(
                                user = user,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                onDarkModeChange = {
                                    viewModel.updateDarkModeEnabled(it)
                                },
                                onNotificationsChange = {
//                                    viewModel.updateNotificationsEnabled(it)
                                },
                                onLanguageChange = {
//                                    viewModel.updateLanguage(it)
                                }
                            )
                        }

                        //App current Version
                        item {
                            AppVersionInfo(
                                modifier = Modifier.padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                    bottom = 16.dp
                                )
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "User profile not found",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            is ProfileViewModel.ProfileState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Error loading profile",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = (userProfileState as ProfileViewModel.ProfileState.Error).message,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }
            }

            null -> {
                // Handle null state if needed
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No profile data available")
                }
            }
        }

        if (logoutConfirmationState) {
            confirmDialogState.show()
        }

        ReusableDialog(
            state = confirmDialogState,
            config = DialogBuilder.confirmation(
                title = "Logout",
                message = "Are you sure you want to logout?",
                onConfirm = {
                    onLogout()
                    viewModel.updateLogoutConfirmationState(false)
                },
                onDismiss = {
                    viewModel.updateLogoutConfirmationState(false)
                }
            )
        )

    }
}

