package com.devvikram.striveo.ui.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

import com.devvikram.striveo.ui.reuseables.TextFields

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCreationScreen(
    viewModel: TaskViewModel = hiltViewModel(),
    onBackPressed: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsState()
    val title by viewModel.title.collectAsState()
    val description by viewModel.description.collectAsState()
    val category by viewModel.category.collectAsState()
    val priority by viewModel.priority.collectAsState()
    val tags by viewModel.tags.collectAsState()
    val currentTag by viewModel.currentTag.collectAsState()
    val validationErrors by viewModel.validationErrors.collectAsState()

    val focusManager = LocalFocusManager.current

    // Handle UI state changes
    LaunchedEffect(uiState) {
        when (uiState) {
            is TaskViewModel.UiState.Success -> {
                kotlinx.coroutines.delay(1500)
                onBackPressed()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Sticky Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
        ) {
            Column {
                // Top App Bar Content
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,

                ) {
                    IconButton(onClick = {
                        onBackPressed()
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Create Task",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = "Add a new task to your workflow",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                Divider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    thickness = 0.5.dp
                )
            }
        }

        // Scrollable Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            // UI State Messages
            when (uiState) {
                is TaskViewModel.UiState.Success -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = Color(0xFF4CAF50)
                                )
                                Text(
                                    text = (uiState as TaskViewModel.UiState.Success).message,
                                    color = Color(0xFF2E7D32),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(start = 12.dp)
                                )
                            }
                        }
                    }
                }
                is TaskViewModel.UiState.Error -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Outlined.ErrorOutline,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = (uiState as TaskViewModel.UiState.Error).message,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(start = 12.dp)
                                )
                            }
                        }
                    }
                }
                else -> {}
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
                TextFields.CommonTextField(
                    value = title,
                    onValueChange = viewModel::updateTitle,
                    label = "Task Title",
                    placeholder = "What needs to be done?",
                    leadingIcon = Icons.Outlined.Title,
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
                TextFields.CommonTextField(
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

            // Priority selection
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

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.createTask() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = uiState !is TaskViewModel.UiState.Loading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when (uiState) {
                            is TaskViewModel.UiState.Success -> Color(0xFF4CAF50)
                            else -> MaterialTheme.colorScheme.primary
                        },
                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 6.dp,
                        disabledElevation = 0.dp
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        when (uiState) {
                            is TaskViewModel.UiState.Loading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Creating...",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            is TaskViewModel.UiState.Success -> {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Created!",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            else -> {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Create Task",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}