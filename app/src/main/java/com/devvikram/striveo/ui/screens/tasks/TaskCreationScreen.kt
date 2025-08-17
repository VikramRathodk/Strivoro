package com.devvikram.striveo.ui.screens.tasks

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devvikram.striveo.config.enums.TaskPriority
import com.devvikram.striveo.room.model.RoomTask

import com.devvikram.striveo.ui.reuseables.TextFields
import com.devvikram.striveo.ui.reuseables.chips.TagChip
import com.devvikram.striveo.ui.reuseables.dialogs.DatePickerDialog
import com.devvikram.striveo.ui.reuseables.dialogs.TimePickerDialog
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCreationScreen(
    viewModel: TaskViewModel = hiltViewModel(),
    onTaskCreated: (RoomTask) -> Unit = {}
) {
    val title by viewModel.title.collectAsState()
    val description by viewModel.description.collectAsState()
    val category by viewModel.category.collectAsState()
    val priority by viewModel.priority.collectAsState()
    val tags by viewModel.tags.collectAsState()
    val currentTag by viewModel.currentTag.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val showSuccessMessage by viewModel.showSuccessMessage.collectAsState()
    val validationErrors by viewModel.validationErrors.collectAsState()

    val focusManager = LocalFocusManager.current

    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                scrollBehavior = topAppBarScrollBehavior,

                title = {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Create Task",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Add a new task to your workflow",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back navigation */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding() + 16.dp,
                bottom = paddingValues.calculateBottomPadding() + 16.dp
            )
        ) {

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            // Validation Errors
            if (validationErrors.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            validationErrors.forEach { error ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                ) {
                                    Icon(
                                        Icons.Outlined.ErrorOutline,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        text = error,
                                        color = MaterialTheme.colorScheme.error,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Title Input
            item {
                TextFields.FormTextField(
                    value = title,
                    onValueChange = viewModel::updateTitle,
                    label = "Task Title",
                    placeholder = "What needs to be done?",
                    icon = Icons.Outlined.Title,
                    isRequired = true,
                    isError = validationErrors.any { it.contains("Title") },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )
            }

            // Description Input
            item {
                TextFields.FormTextField(
                    value = description,
                    onValueChange = viewModel::updateDescription,
                    label = "Description",
                    placeholder = "Add more details...",
                    minLines = 3,
                    maxLines = 5
                )
            }

            // Category & Priority Row
            item {

                Box(modifier = Modifier.fillMaxWidth()) {
                    CategorySelector(
                        selectedCategory = category,
                        predefinedCategories = viewModel.predefinedCategories,
                        onCategorySelected = viewModel::updateCategory,
                        isError = validationErrors.any { it.contains("Category") }
                    )
                }
            }

            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    PrioritySelector(
                        selectedPriority = priority,
                        onPrioritySelected = viewModel::updatePriority
                    )
                }
            }

            // Tags Section
            item {
                TagsSection(
                    tags = tags,
                    currentTag = currentTag,
                    suggestedTags = viewModel.suggestedTags,
                    onCurrentTagChanged = viewModel::updateCurrentTag,
                    onAddTag = viewModel::addTag,
                    onRemoveTag = viewModel::removeTag,
                    onAddSuggestedTag = viewModel::addSuggestedTag
                )
            }

            // Create Button
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.createTask() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Create Task",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Success Message
        if (showSuccessMessage) {
            LaunchedEffect(showSuccessMessage) {
                kotlinx.coroutines.delay(2000)
                viewModel.hideSuccessMessage()
            }

            Snackbar(
                modifier = Modifier
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                containerColor = MaterialTheme.colorScheme.inverseSurface,
                contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                action = {
                    TextButton(onClick = viewModel::hideSuccessMessage) {
                        Text("OK", color = MaterialTheme.colorScheme.primary)
                    }
                }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Task created successfully!")
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategorySelector(
    selectedCategory: String,
    predefinedCategories: List<String>,
    onCategorySelected: (String) -> Unit,
    isError: Boolean
) {
    var showDropdown by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "Category *",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = showDropdown,
            onExpandedChange = { showDropdown = !showDropdown }
        ) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = onCategorySelected,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                placeholder = {
                    Text(
                        "Select category",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Category,
                        contentDescription = null,
                        tint = if (isError) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = showDropdown)
                },
                isError = isError,
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            )

            ExposedDropdownMenu(
                expanded = showDropdown,
                onDismissRequest = { showDropdown = false },
                modifier = Modifier.background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(12.dp)
                )
            ) {
                predefinedCategories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            onCategorySelected(category)
                            showDropdown = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PrioritySelector(
    selectedPriority: TaskPriority,
    onPrioritySelected: (TaskPriority) -> Unit
) {
    Column {
        Text(
            text = "Priority",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(TaskPriority.values()) { priority ->
                val isSelected = selectedPriority == priority
                val (color, bgColor) = when (priority) {
                    TaskPriority.HIGH -> Color(0xFFFF5252) to Color(0xFFFFEBEE)
                    TaskPriority.MEDIUM -> Color(0xFFFF9800) to Color(0xFFFFF3E0)
                    TaskPriority.LOW -> Color(0xFF4CAF50) to Color(0xFFE8F5E8)
                }

                Card(
                    modifier = Modifier
                        .clickable { onPrioritySelected(priority) }
                        .animateContentSize(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) bgColor else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) color else MaterialTheme.colorScheme.outline.copy(
                            alpha = 0.3f
                        )
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(color, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = priority.name.lowercase().replaceFirstChar { it.uppercase() },
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) color else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TagsSection(
    tags: List<String>,
    currentTag: String,
    suggestedTags: List<String>,
    onCurrentTagChanged: (String) -> Unit,
    onAddTag: () -> Unit,
    onRemoveTag: (String) -> Unit,
    onAddSuggestedTag: (String) -> Unit
) {
    Column {
        Text(
            text = "Tags",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Add tag input
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = currentTag,
                onValueChange = onCurrentTagChanged,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        "Add tag...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Tag,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onAddTag() }),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            FilledIconButton(
                onClick = onAddTag,
                enabled = currentTag.isNotBlank(),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add tag",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        // Current tags
        if (tags.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tags) { tag ->
                    TagChip(
                        tag = tag,
                        onRemove = { onRemoveTag(tag) },
                        isSelected = true
                    )
                }
            }
        }

        // Suggested tags
        if (suggestedTags.filter { !tags.contains(it) }.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Suggested:",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(suggestedTags.filter { !tags.contains(it) }) { tag ->
                    TagChip(
                        tag = tag,
                        onRemove = { onAddSuggestedTag(tag) },
                        isSelected = false
                    )
                }
            }
        }
    }
}




