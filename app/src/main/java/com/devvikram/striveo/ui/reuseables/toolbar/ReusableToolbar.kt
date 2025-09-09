package com.devvikram.striveo.ui.reuseables.toolbar

import androidx.compose.ui.unit.Dp

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * A reusable toolbar component for Jetpack Compose applications
 *
 * @param title The title text to display in the toolbar
 * @param modifier Modifier for the toolbar
 * @param navigationIcon Optional navigation icon (typically back arrow or menu)
 * @param onNavigationClick Callback for navigation icon click
 * @param backgroundColor Background color of the toolbar
 * @param contentColor Content color (text and icons)
 * @param elevation Elevation of the toolbar
 * @param actions Composable content for toolbar actions (typically icons on the right)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReusableToolbar(
    title: @Composable () -> Unit = {},
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit= {},
    onNavigationClick: (() -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    elevation: Dp = 4.dp,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = title,
        modifier = modifier,
        navigationIcon = navigationIcon,
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = backgroundColor,
            titleContentColor = contentColor,
            navigationIconContentColor = contentColor,
            actionIconContentColor = contentColor
        )
    )
}

@Composable
fun CustomToolbar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    elevation: Dp = 4.dp,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = backgroundColor,
        tonalElevation = elevation
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Navigation Icon
            navigationIcon?.let { navIcon ->
                IconButton(onClick = { }) {
                    CompositionLocalProvider(LocalContentColor provides contentColor) {
                        navIcon()
                    }
                }
            }

            // Title
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                CompositionLocalProvider(LocalContentColor provides contentColor) {
                    title()
                }
            }

            // Actions
            CompositionLocalProvider(LocalContentColor provides contentColor) {
                Row {
                    actions()
                }
            }
        }
    }
}

// Preview examples
@Preview(showBackground = true)
@Composable
fun ToolbarPreview() {
    MaterialTheme {
        Column {
            // Basic toolbar with back arrow
            ReusableToolbar(
                title = {
                    Text(
                        text = "Back Toolbar",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                },
                onNavigationClick = { /* Handle back click */ },
                actions = {
                    IconButton(onClick = { /* Handle search */ }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                    IconButton(onClick = { /* Handle more */ }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Toolbar with menu icon
            ReusableToolbar(
                title ={
                    Text(
                        text = "Menu Toolbar",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu"
                    )
                },
                backgroundColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                actions = {
                    IconButton(onClick = { /* Handle action */ }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Custom toolbar with composable title
            CustomToolbar(
                title = {
                    Column {
                        Text(
                            text = "Custom Title",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Subtitle",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                backgroundColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary,
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More"
                        )
                    }
                }
            )
        }
    }
}
