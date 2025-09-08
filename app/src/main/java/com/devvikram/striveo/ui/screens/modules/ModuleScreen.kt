package com.devvikram.striveo.ui.screens.modules

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devvikram.striveo.AppUtils
import com.devvikram.striveo.room.model.RoomModule

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleScreen(
    viewModel: ModuleViewModel = hiltViewModel<ModuleViewModel>(),
    onBackClick: () -> Unit = {}
) {
    val moduleState = viewModel.moduleState.collectAsState()
    var moduleToDelete by remember { mutableStateOf<RoomModule?>(null) }
    var showAddBottomSheet by remember { mutableStateOf(false) }
    var moduleToEdit by remember { mutableStateOf<RoomModule?>(null) }
    val context = LocalContext.current

    val addBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val editBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Modules",
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showAddBottomSheet = true
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Module"
                )
            }
        }
    ) { paddingValues ->

        when (val state = moduleState.value) {
            is ModuleViewModel.ModuleState.Idle -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No modules loaded",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            is ModuleViewModel.ModuleState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = "Loading modules...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            is ModuleViewModel.ModuleState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "Error",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }

            is ModuleViewModel.ModuleState.Success -> {
                if (state.modules.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "No modules found",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Add your first module by tapping the + button",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = state.modules,
                            key = { module -> module.moduleId }
                        ) { module ->
                            ModuleCard(
                                module = module,
                                onEditClick = {
                                    moduleToEdit = module
                                },
                                onDeleteClick = { moduleToDelete = module },
                                onAssignToClick = {
                                    AppUtils.showToast(
                                        context = context,
                                        message = "Will Available Soon"
                                    )
                                },
                                onDuplicateClick = {
                                    AppUtils.showToast(
                                        context = context,
                                        message = "Will Available Soon"
                                    )
                                },
                                onArchiveClick = {
                                    AppUtils.showToast(
                                        context = context,
                                        message = "Will Available Soon"
                                    )
                                },
                                onShareClick = {
                                    AppUtils.showToast(
                                        context = context,
                                        message = "Will Available Soon"
                                    )

                                },
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Module Bottom Sheet
    if (showAddBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddBottomSheet = false },
            sheetState = addBottomSheetState,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            AddModuleBottomSheetContent(
                onDismiss = { showAddBottomSheet = false },
                onSave = { newModule ->
                    viewModel.addModule(newModule)
                    showAddBottomSheet = false
                },
                projectList = viewModel.getProjects()
            )
        }
    }

    // Edit Module Bottom Sheet
    moduleToEdit?.let { module ->
        ModalBottomSheet(
            onDismissRequest = { moduleToEdit = null },
            sheetState = editBottomSheetState,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            EditModuleBottomSheetContent(
                module = module,
                onDismiss = { moduleToEdit = null },
                onSave = { updatedModule ->
                    viewModel.updateModule(updatedModule)
                    moduleToEdit = null
                },
                projectList = viewModel.getProjects()
            )
        }
    }

    // Delete Confirmation Dialog
    moduleToDelete?.let { module ->
        AlertDialog(
            onDismissRequest = { moduleToDelete = null },
            title = { Text("Delete Module") },
            text = {
                Text("Are you sure you want to delete '${module.title}'? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteModule(module.moduleId)
                        moduleToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { moduleToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}


