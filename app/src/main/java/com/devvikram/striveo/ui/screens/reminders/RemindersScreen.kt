// Reminder.kt (Data classes for reminders)
package com.devvikram.striveo.ui.screens.reminders

// RemindersScreen.kt

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Reminder(
    val id: String,
    val title: String,
    val description: String = "",
    val dateTime: LocalDateTime,
    val priority: ReminderPriority,
    val category: ReminderCategory,
    val isCompleted: Boolean = false,
    val isRecurring: Boolean = false,
    val recurrenceType: RecurrenceType = RecurrenceType.NONE,
    val tags: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

enum class ReminderPriority(val displayName: String, val color: Color) {
    HIGH("High", Color(0xFFE57373)),
    MEDIUM("Medium", Color(0xFFFFB74D)),
    LOW("Low", Color(0xFF81C784))
}

enum class ReminderCategory(val displayName: String, val color: Color) {
    WORK("Work", Color(0xFF42A5F5)),
    PERSONAL("Personal", Color(0xFF66BB6A)),
    HEALTH("Health", Color(0xFFEF5350)),
    FINANCE("Finance", Color(0xFFFFCA28)),
    EDUCATION("Education", Color(0xFFAB47BC)),
    OTHER("Other", Color(0xFF78909C))
}

enum class RecurrenceType(val displayName: String) {
    NONE("None"),
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    YEARLY("Yearly")
}

enum class ReminderFilter {
    ALL, TODAY, UPCOMING, COMPLETED, HIGH_PRIORITY
}



data class ReminderStats(
    val totalReminders: Int,
    val completedReminders: Int,
    val pendingReminders: Int,
    val todayReminders: Int,
    val highPriorityReminders: Int
)




@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    viewModel: RemindersViewModel = hiltViewModel()
) {
    val filteredReminders by viewModel.filteredReminders.collectAsState(initial = emptyList())
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val showAddDialog by viewModel.showAddDialog.collectAsState()
    val stats = viewModel.getReminderStats()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header with title and add button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Reminders",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            FloatingActionButton(
                onClick = viewModel::showAddDialog,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Reminder")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Statistics Cards
        ReminderStatsSection(stats = stats)

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        SearchTextField(
            query = searchQuery,
            onQueryChange = viewModel::updateSearchQuery,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Filter Chips
        FilterChipsSection(
            selectedFilter = selectedFilter,
            onFilterSelected = viewModel::setFilter
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Reminders List
        if (filteredReminders.isEmpty()) {
            EmptyState(filter = selectedFilter)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredReminders) { reminder ->
                    ReminderCard(
                        reminder = reminder,
                        onToggleComplete = { viewModel.toggleReminderCompletion(reminder.id) },
                        onDelete = { viewModel.deleteReminder(reminder.id) }
                    )
                }
            }
        }
    }

    // Add Reminder Dialog
    if (showAddDialog) {
        AddReminderDialog(
            onDismiss = viewModel::hideAddDialog,
            onAddReminder = { reminder ->
                viewModel.addReminder(reminder)
                viewModel.hideAddDialog()
            }
        )
    }
}

@Composable
private fun ReminderStatsSection(stats: ReminderStats) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            StatCard(
                title = "Total",
                value = stats.totalReminders.toString(),
                color = MaterialTheme.colorScheme.primary
            )
        }
        item {
            StatCard(
                title = "Pending",
                value = stats.pendingReminders.toString(),
                color = Color(0xFFFF9800)
            )
        }
        item {
            StatCard(
                title = "Today",
                value = stats.todayReminders.toString(),
                color = Color(0xFF4CAF50)
            )
        }
        item {
            StatCard(
                title = "High Priority",
                value = stats.highPriorityReminders.toString(),
                color = Color(0xFFF44336)
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    color: Color
) {
    Card(
        modifier = Modifier
            .width(90.dp)
            .height(70.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                fontSize = 10.sp,
                color = color
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTextField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Search reminders...") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search")
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun FilterChipsSection(
    selectedFilter: ReminderFilter,
    onFilterSelected: (ReminderFilter) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(ReminderFilter.values()) { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = {
                    Text(
                        text = when (filter) {
                            ReminderFilter.ALL -> "All"
                            ReminderFilter.TODAY -> "Today"
                            ReminderFilter.UPCOMING -> "Upcoming"
                            ReminderFilter.COMPLETED -> "Completed"
                            ReminderFilter.HIGH_PRIORITY -> "High Priority"
                        }
                    )
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ReminderCard(
    reminder: Reminder,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (reminder.isCompleted)
                MaterialTheme.colorScheme.surfaceVariant
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Checkbox(
                    checked = reminder.isCompleted,
                    onCheckedChange = { onToggleComplete() }
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = reminder.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        textDecoration = if (reminder.isCompleted) TextDecoration.LineThrough else null,
                        color = if (reminder.isCompleted)
                            MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onSurface
                    )

                    if (reminder.description.isNotEmpty()) {
                        Text(
                            text = reminder.description,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // DateTime
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = reminder.dateTime.format(
                                    DateTimeFormatter.ofPattern("MMM dd, HH:mm")
                                ),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Category
                        Text(
                            text = reminder.category.displayName,
                            fontSize = 10.sp,
                            color = reminder.category.color,
                            modifier = Modifier
                                .background(
                                    reminder.category.color.copy(alpha = 0.2f),
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Priority indicator
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    reminder.priority.color,
                                    CircleShape
                                )
                        )

                        if (reminder.isRecurring) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Recurring",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Tags
                    if (reminder.tags.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(reminder.tags) { tag ->
                                Text(
                                    text = "#$tag",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .background(
                                            MaterialTheme.colorScheme.primaryContainer,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState(filter: ReminderFilter) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = when (filter) {
                ReminderFilter.ALL -> "No reminders yet"
                ReminderFilter.TODAY -> "No reminders for today"
                ReminderFilter.UPCOMING -> "No upcoming reminders"
                ReminderFilter.COMPLETED -> "No completed reminders"
                ReminderFilter.HIGH_PRIORITY -> "No high priority reminders"
            },
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Tap the + button to add your first reminder",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddReminderDialog(
    onDismiss: () -> Unit,
    onAddReminder: (Reminder) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(ReminderPriority.MEDIUM) }
    var selectedCategory by remember { mutableStateOf(ReminderCategory.PERSONAL) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Reminder") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Priority", fontWeight = FontWeight.Medium)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    items(ReminderPriority.entries.toTypedArray()) { priority ->
                        FilterChip(
                            selected = selectedPriority == priority,
                            onClick = { selectedPriority = priority },
                            label = { Text(priority.displayName) }
                        )
                    }
                }

                Text("Category", fontWeight = FontWeight.Medium)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    items(ReminderCategory.entries.toTypedArray()) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category.displayName) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onAddReminder(
                            Reminder(
                                id = System.currentTimeMillis().toString(),
                                title = title,
                                description = description,
                                dateTime = LocalDateTime.now().plusHours(1),
                                priority = selectedPriority,
                                category = selectedCategory
                            )
                        )
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}