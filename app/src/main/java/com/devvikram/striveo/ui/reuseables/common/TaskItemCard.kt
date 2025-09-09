package com.devvikram.striveo.ui.reuseables.common

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devvikram.striveo.config.enums.TaskPriority
import com.devvikram.striveo.config.enums.TaskStatus
import com.devvikram.striveo.room.model.RoomTask
import com.devvikram.striveo.ui.screens.home.HomeViewModel

@Composable
fun TaskItemCard(
    roomTask: RoomTask,
    onToggle: () -> Unit,
    onClick: () -> Unit,
    onStatusChange: (TaskStatus) -> Unit = {},
    viewModel: HomeViewModel
) {
    // Get task-specific state
    val taskStateUpdate = viewModel.getTaskUpdateState(roomTask.taskId)
    var showStatusDropdown by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (roomTask.isCompleted)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.surface
        ),

        border = when (taskStateUpdate) {
            is HomeViewModel.TaskUpdateState.Loading -> BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
            is HomeViewModel.TaskUpdateState.Success -> BorderStroke(
                1.dp,
                Color(0xFF10B981).copy(alpha = 0.7f)
            )
            is HomeViewModel.TaskUpdateState.Failure -> BorderStroke(
                1.dp,
                Color(0xFFEF4444).copy(alpha = 0.7f)
            )
            else -> BorderStroke(
                0.5.dp,
                MaterialTheme.colorScheme.outline.copy(
                    0.3f
                )
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header Row - Title and Checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Title
                Text(
                    text = roomTask.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        textDecoration = if (roomTask.isCompleted)
                            TextDecoration.LineThrough
                        else TextDecoration.None
                    ),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = if (roomTask.isCompleted)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Checkbox with loading state
                Box {
                    IconButton(
                        onClick = {
                            onToggle()
                        },
                        modifier = Modifier.size(28.dp),
                        enabled = taskStateUpdate !is HomeViewModel.TaskUpdateState.Loading
                    ) {
                        when (taskStateUpdate) {
                            is HomeViewModel.TaskUpdateState.Loading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            else -> {
                                Icon(
                                    imageVector = if (roomTask.status == TaskStatus.COMPLETED)
                                        Icons.Filled.CheckCircle
                                    else Icons.Outlined.Circle,
                                    contentDescription = if (roomTask.isCompleted) "Completed" else "Mark complete",
                                    tint = if (roomTask.status == TaskStatus.COMPLETED)
                                        Color(0xFF10B981)
                                    else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Show update status message
            if (taskStateUpdate is HomeViewModel.TaskUpdateState.Failure) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = taskStateUpdate.errorMessage,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFEF4444)
                    )
                }
            }

            // Description
            if (roomTask.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = roomTask.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (roomTask.isCompleted)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Metadata Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side - Priority and Category
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Priority Chip
                    if (roomTask.priority != null) {
                        Surface(
                            color = when (roomTask.priority) {
                                TaskPriority.HIGH.name -> Color(0xFFEF4444).copy(alpha = 0.1f)
                                TaskPriority.MEDIUM.name -> Color(0xFFF59E0B).copy(alpha = 0.1f)
                                TaskPriority.LOW.name -> Color(0xFF10B981).copy(alpha = 0.1f)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = roomTask.priority.lowercase().replaceFirstChar { it.uppercase() },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = when (roomTask.priority) {
                                    TaskPriority.HIGH.name -> Color(0xFFEF4444)
                                    TaskPriority.MEDIUM.name -> Color(0xFFF59E0B)
                                    TaskPriority.LOW.name -> Color(0xFF10B981)
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Category
                    if (roomTask.category.isNotEmpty() && roomTask.priority != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "•",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    if (roomTask.category.isNotEmpty()) {
                        Text(
                            text = roomTask.category,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                // Right side - Clickable Status with Dropdown
                Box {
                    Surface(
                        color = when (roomTask.status) {
                            TaskStatus.NOT_STARTED -> MaterialTheme.colorScheme.surfaceVariant
                            TaskStatus.IN_PROGRESS -> Color(0xFFFEF3C7)
                            TaskStatus.ON_HOLD -> Color(0xFFFED7AA)
                            TaskStatus.REVIEW -> Color(0xFFDBEAFE)
                            TaskStatus.COMPLETED -> Color(0xFFDCFCE7)
                            TaskStatus.CANCELLED -> Color(0xFFFEE2E2)
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.clickable {
                            if (taskStateUpdate !is HomeViewModel.TaskUpdateState.Loading) {
                                showStatusDropdown = true
                            }
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Show loading indicator for status updates
                            if (taskStateUpdate is HomeViewModel.TaskUpdateState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(12.dp),
                                    strokeWidth = 1.5.dp,
                                    color = when (roomTask.status) {
                                        TaskStatus.NOT_STARTED -> MaterialTheme.colorScheme.onSurfaceVariant
                                        TaskStatus.IN_PROGRESS -> Color(0xFF92400E)
                                        TaskStatus.ON_HOLD -> Color(0xFF7C2D12)
                                        TaskStatus.REVIEW -> Color(0xFF1E40AF)
                                        TaskStatus.COMPLETED -> Color(0xFF166534)
                                        TaskStatus.CANCELLED -> Color(0xFF991B1B)
                                    }
                                )
                            }

                            Text(
                                text = roomTask.status.name.replace("_", " ").lowercase()
                                    .split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } },
                                style = MaterialTheme.typography.labelSmall,
                                color = when (roomTask.status) {
                                    TaskStatus.NOT_STARTED -> MaterialTheme.colorScheme.onSurfaceVariant
                                    TaskStatus.IN_PROGRESS -> Color(0xFF92400E)
                                    TaskStatus.ON_HOLD -> Color(0xFF7C2D12)
                                    TaskStatus.REVIEW -> Color(0xFF1E40AF)
                                    TaskStatus.COMPLETED -> Color(0xFF166534)
                                    TaskStatus.CANCELLED -> Color(0xFF991B1B)
                                },
                                fontWeight = FontWeight.Medium
                            )

                            if (taskStateUpdate !is HomeViewModel.TaskUpdateState.Loading) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Change status",
                                    modifier = Modifier.size(14.dp),
                                    tint = when (roomTask.status) {
                                        TaskStatus.NOT_STARTED -> MaterialTheme.colorScheme.onSurfaceVariant
                                        TaskStatus.IN_PROGRESS -> Color(0xFF92400E)
                                        TaskStatus.ON_HOLD -> Color(0xFF7C2D12)
                                        TaskStatus.REVIEW -> Color(0xFF1E40AF)
                                        TaskStatus.COMPLETED -> Color(0xFF166534)
                                        TaskStatus.CANCELLED -> Color(0xFF991B1B)
                                    }.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    // Status Dropdown Menu
                    DropdownMenu(
                        expanded = showStatusDropdown,
                        onDismissRequest = { showStatusDropdown = false }
                    ) {
                        TaskStatus.values().forEach { status ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Status color indicator
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .background(
                                                    when (status) {
                                                        TaskStatus.NOT_STARTED -> MaterialTheme.colorScheme.surfaceVariant
                                                        TaskStatus.IN_PROGRESS -> Color(0xFFFEF3C7)
                                                        TaskStatus.ON_HOLD -> Color(0xFFFED7AA)
                                                        TaskStatus.REVIEW -> Color(0xFFDBEAFE)
                                                        TaskStatus.COMPLETED -> Color(0xFFDCFCE7)
                                                        TaskStatus.CANCELLED -> Color(0xFFFEE2E2)
                                                    },
                                                    CircleShape
                                                )
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = status.name.replace("_", " ").lowercase()
                                                .split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } },
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                },
                                onClick = {
                                    onStatusChange(status)
                                    viewModel.updateTaskStatus(roomTask.taskId, status)
                                    showStatusDropdown = false
                                }
                            )
                        }
                    }
                }
            }
            // Collect flows
            val projectName by viewModel.getProjectNameFlow(roomTask.projectId).collectAsState(initial = "")
            val moduleName by viewModel.getModuleNameFlow(roomTask.moduleId).collectAsState(initial = "")

// Show chips row
            if (projectName.isNotEmpty() || moduleName.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp) // spacing between project & module
                ) {
                    if (projectName.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Project:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Surface(
                                shape = RoundedCornerShape(50),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                contentColor = MaterialTheme.colorScheme.primary,
                                tonalElevation = 2.dp
                            ) {
                                Text(
                                    text = projectName,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }

                    if (moduleName.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Module:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Surface(
                                shape = RoundedCornerShape(50),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                contentColor = MaterialTheme.colorScheme.primary,
                                tonalElevation = 2.dp
                            ) {
                                Text(
                                    text = moduleName,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            }




            // Time and Due Date (only if present)
            val hasTimeInfo = roomTask.estimatedTime.isNotEmpty() || roomTask.dueDate.isNotEmpty()
            if (hasTimeInfo) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Estimated Time
                    if (roomTask.estimatedTime.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
                                contentDescription = "Estimated time",
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = roomTask.estimatedTime,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    // Due Date
                    if (roomTask.dueDate.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CalendarToday,
                                contentDescription = "Due date",
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = roomTask.dueDate,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Tags
            if (roomTask.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(roomTask.tags.take(3)) { tag ->
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "#$tag",
                                modifier = Modifier.padding(
                                    horizontal = 6.dp,
                                    vertical = 2.dp
                                ),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    if (roomTask.tags.size > 3) {
                        item {
                            Text(
                                text = "+${roomTask.tags.size - 3} more",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}