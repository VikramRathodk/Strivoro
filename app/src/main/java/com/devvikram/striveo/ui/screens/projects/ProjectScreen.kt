package com.devvikram.striveo.ui.screens.projects

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devvikram.striveo.AppUtils
import com.devvikram.striveo.room.model.RoomProject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectScreen(
    viewModel: ProjectViewModel = hiltViewModel<ProjectViewModel>(),
    onBackClick: () -> Unit = {}
) {
    val projectsUiState by viewModel.projectsUiState.collectAsState()
    val showAddProjectDialog by viewModel.showAddProjectDialog.collectAsState()

    Scaffold(
        topBar = {
            ProjectsTopBar(
                onBackClick = {
                    onBackClick()
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddProjectDialog() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Project"
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (projectsUiState) {
                is ProjectViewModel.ProjectsUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                is ProjectViewModel.ProjectsUiState.Empty -> {
                    EmptyProjectsState(
                        onAddProjectClick = { viewModel.showAddProjectDialog() }
                    )
                }

                is ProjectViewModel.ProjectsUiState.Success -> {
                    ProjectsList(
                        viewModel = viewModel,
                        context = LocalContext.current,
                        projects = (projectsUiState as ProjectViewModel.ProjectsUiState.Success).projects,
                        onDeleteProject = { viewModel.deleteProject(it.projectId) },
                        onUpdateProject = { viewModel.updateProject(it) }
                    )
                }

                is ProjectViewModel.ProjectsUiState.Error -> {
                    ErrorState(
                        message = (projectsUiState as ProjectViewModel.ProjectsUiState.Error).message,
                        onRetry = { /* Implement retry logic */ }
                    )
                }
            }
        }
    }

    // Add Project Dialog
    if (showAddProjectDialog) {
        AddProjectBottomSheet(
            viewModel = viewModel,
            onDismiss = { viewModel.hideAddProjectDialog() }
        )
    }
}

@Composable
private fun ProjectsList(
    viewModel: ProjectViewModel,
    context: Context,
    projects: List<RoomProject>,
    onDeleteProject: (RoomProject) -> Unit,
    navigateToProjectDetails: () -> Unit = {},
    onUpdateProject: (RoomProject) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(projects) { project ->
            ProjectCard(
                viewModel = viewModel,
                project = project,
                onDeleteClick = { onDeleteProject(project) },
                navigateToProjectDetails = {
                    AppUtils.showToast(context, "Will available Soon...")
                },
                onUpdateProject = {
                    onUpdateProject(it)
                }
            )
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Something went wrong",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        FilledTonalButton(onClick = onRetry) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Retry")
        }
    }
}



