package com.devvikram.striveo.ui.screens.projects

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.devvikram.striveo.AppUtils.Companion.formatDate
import com.devvikram.striveo.room.model.RoomProject

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ProjectCard(
    modifier: Modifier = Modifier,
    viewModel: ProjectViewModel,
    project: RoomProject,
    onDeleteClick: () -> Unit,
    navigateToProjectDetails: () -> Unit = {},
    onUpdateProject: (RoomProject) -> Unit = {},
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showUpdateProjectDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }

    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    val isEdited = project.lastModifiedAt != project.createdAt

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(150),
        label = "card_scale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .combinedClickable(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    navigateToProjectDetails()
                },
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    showMenu = true
                }
            ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Project Avatar
            ProjectAvatar(
                letter = project.projectName.firstOrNull()?.uppercase() ?: "P",
                isEdited = isEdited
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Project Info
            Column(modifier = Modifier.weight(1f)) {
                ProjectTitle(
                    name = project.projectName,
                    isEdited = isEdited
                )

                if (project.description.isNotEmpty()) {
                    ProjectDescription(project.description)
                }

                Spacer(modifier = Modifier.height(8.dp))

                ProjectMetadata(
                    createdAt = project.createdAt,
                    lastModifiedAt = if (isEdited) project.lastModifiedAt else 0,
                    context = context
                )
            }

            // Menu Button
            ProjectMenuButton(
                showMenu = showMenu,
                onMenuToggle = { showMenu = it },
                onEdit = { showUpdateProjectDialog = true },
                onDelete = { showDeleteDialog = true }
            )
        }
    }

    // Dialogs
    if (showDeleteDialog) {
        DeleteProjectDialog(
            projectName = project.projectName,
            onConfirm = {
                onDeleteClick()
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    if (showUpdateProjectDialog) {
        UpdateProjectBottomSheet(
            viewModel = viewModel,
            project = project,
            onDismiss = { showUpdateProjectDialog = false },
            onUpdateProject = {
                onUpdateProject(it)
                showUpdateProjectDialog = false
            }
        )
    }
}

@Composable
private fun ProjectAvatar(
    letter: String,
    isEdited: Boolean
) {
    Box {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = letter,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

//        if (isEdited) {
//            Surface(
//                modifier = Modifier
//                    .align(Alignment.BottomEnd)
//                    .offset(x = 2.dp, y = 2.dp)
//                    .size(16.dp),
//                shape = CircleShape,
//                color = MaterialTheme.colorScheme.primary
//            ) {
//                Icon(
//                    imageVector = Icons.Outlined.Edit,
//                    contentDescription = "Edited",
//                    modifier = Modifier
//                        .padding(3.dp)
//                        .size(10.dp),
//                    tint = MaterialTheme.colorScheme.onPrimary
//                )
//            }
//        }
    }
}

@Composable
private fun ProjectTitle(
    name: String,
    isEdited: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false)
        )

        if (isEdited) {
            Badge(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(
                    modifier = Modifier.padding( 4.dp),
                    text = "EDITED",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
private fun ProjectDescription(description: String) {
    Text(
        text = description,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.padding(top = 2.dp)
    )
}

@Composable
private fun ProjectMetadata(
    createdAt: Long,
    lastModifiedAt: Long,
    context: android.content.Context
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MetadataChip(
            icon = Icons.Outlined.CalendarToday,
            text = formatDate(
                createdAt,
                context
            )
        )
        if (lastModifiedAt != 0L) {
            MetadataChip(
                icon = Icons.Outlined.Edit,
                text = formatDate(
                    lastModifiedAt.toLong(),
                    context
                ),
                tint = MaterialTheme.colorScheme.primary
            )
        }

    }
}

@Composable
private fun MetadataChip(
    icon: ImageVector,
    text: String,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = tint
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = tint
        )
    }
}

@Composable
private fun ProjectMenuButton(
    showMenu: Boolean,
    onMenuToggle: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Box {
        IconButton(
            onClick = { onMenuToggle(true) },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More options",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        DropdownMenu(
            modifier = Modifier.padding(8.dp),
            containerColor = MaterialTheme.colorScheme.background,
            expanded = showMenu,
            onDismissRequest = { onMenuToggle(false) }
        ) {
            DropdownMenuItem(
                text = { Text("Edit") },
                onClick = {
                    onMenuToggle(false)
                    onEdit()
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
            DropdownMenuItem(
                text = { Text("Delete") },
                onClick = {
                    onMenuToggle(false)
                    onDelete()
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            )
        }
    }
}

@Composable
private fun DeleteProjectDialog(
    projectName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Project?", fontWeight = FontWeight.SemiBold) },
        text = { Text("Delete '$projectName'? This action cannot be undone.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    "Delete",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}