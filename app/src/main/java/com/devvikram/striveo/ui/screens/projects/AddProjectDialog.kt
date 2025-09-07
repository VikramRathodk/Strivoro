package com.devvikram.striveo.ui.screens.projects

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun AddProjectDialog(
    viewModel: ProjectViewModel,
    onDismiss: () -> Unit
) {
    val projectName by viewModel.projectName.collectAsState()
    val projectDescription by viewModel.projectDescription.collectAsState()
    val addProjectUiState by viewModel.addProjectUiState.collectAsState()

    LaunchedEffect(addProjectUiState) {
        if (addProjectUiState is ProjectViewModel.AddProjectUiState.Success) {
            onDismiss()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Project") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = projectName,
                    onValueChange = viewModel::updateProjectName,
                    label = { Text("Project Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )

                OutlinedTextField(
                    value = projectDescription,
                    onValueChange = viewModel::updateProjectDescription,
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )

                AnimatedVisibility(
                    visible = addProjectUiState is ProjectViewModel.AddProjectUiState.Error
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = (addProjectUiState as? ProjectViewModel.AddProjectUiState.Error)?.message
                                ?: "Unknown error",
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (projectName.isNotBlank()) {
                        viewModel.addProject()
                    }
                },
                enabled = projectName.isNotBlank() && addProjectUiState !is ProjectViewModel.AddProjectUiState.Loading
            ) {
                if (addProjectUiState is ProjectViewModel.AddProjectUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Create")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}