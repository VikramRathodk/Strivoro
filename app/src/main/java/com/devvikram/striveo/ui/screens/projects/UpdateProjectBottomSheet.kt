package com.devvikram.striveo.ui.screens.projects

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.devvikram.striveo.AppUtils
import com.devvikram.striveo.room.model.RoomProject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProjectBottomSheet(
    viewModel: ProjectViewModel,
    project: RoomProject,
    onDismiss: () -> Unit,
    onUpdateProject: (RoomProject) -> Unit = {}
) {

    val updateProjectState by viewModel.updateProjectUiState.collectAsState()

    var projectName by remember { mutableStateOf(project.projectName) }
    var projectDescription by remember { mutableStateOf(project.description) }

    val context = LocalContext.current

    // Handle state changes
    LaunchedEffect(updateProjectState) {
        when (updateProjectState) {

            is ProjectViewModel.UpdateProjectUiState.Success -> {
                AppUtils.showToast(
                    context = context,
                    message = (updateProjectState as ProjectViewModel.UpdateProjectUiState.Success).message
                )
                onDismiss()
                viewModel.resetUpdateProjectState()
            }
            is ProjectViewModel.UpdateProjectUiState.Error -> {
                AppUtils.showToast(
                    context = context,
                    message = (updateProjectState as ProjectViewModel.UpdateProjectUiState.Error).message.toString()
                )
            }
            else -> { /* Loading or Idle states handled by UI */ }
        }
    }

    // Validation states
    val isProjectNameValid = projectName.trim().isNotEmpty()
    val isLoading = updateProjectState is ProjectViewModel.UpdateProjectUiState.Loading
    val canUpdate = isProjectNameValid && !isLoading

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight(0.7f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Update Project",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(
                    onClick = onDismiss,
                    enabled = !isLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Project Name Field
            OutlinedTextField(
                value = projectName,
                onValueChange = { projectName = it },
                label = { Text("Project Name") },
                placeholder = { Text("Enter project name") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading,
                isError = !isProjectNameValid && projectName.isNotEmpty(),
                supportingText = {
                    if (!isProjectNameValid && projectName.isNotEmpty()) {
                        Text(
                            text = "Project name cannot be empty",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Project Description Field
            OutlinedTextField(
                value = projectDescription,
                onValueChange = { projectDescription = it },
                label = { Text("Description (Optional)") },
                placeholder = { Text("Enter project description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 4,
                enabled = !isLoading,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Error message display
            if (updateProjectState is ProjectViewModel.UpdateProjectUiState.Error) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = (updateProjectState as ProjectViewModel.UpdateProjectUiState.Error).message.toString(),
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Cancel Button
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading
                ) {
                    Text("Cancel")
                }

                // Update Button
                Button(
                    onClick = {
                        if (canUpdate) {
                            val updatedProject = project.copy(
                                projectName = projectName.trim(),
                                description = projectDescription.trim(),
                                lastModifiedAt = System.currentTimeMillis()
                            )
                            viewModel.updateProject(updatedProject)
                            onUpdateProject(updatedProject)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = canUpdate
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Update")
                    }
                }
            }

            // Bottom padding for better spacing
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}