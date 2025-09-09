package com.devvikram.striveo.ui.reuseables.dropdowns

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlin.math.min

@Composable
fun <T> SearchableDropdown(
    modifier: Modifier = Modifier,
    label: String,
    items: List<T>,
    selectedItem: T?,
    placeholder: String,
    searchPlaceholder: String = "Search...",
    leadingIcon: ImageVector,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onItemSelected: (T) -> Unit,
    getItemTitle: (T) -> String,
    getItemDescription: (T) -> String = { "" },
    getItemId: (T) -> Any,
    isError: Boolean = false,
    errorMessage: String = "",
    maxVisibleItems: Int = 6,
    isSearchEnabled: Boolean = true
) {
    var searchQuery by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "dropdown_rotation"
    )

    // Filter items based on search query
    val filteredItems = remember(items, searchQuery) {
        if (searchQuery.isEmpty() || !isSearchEnabled) {
            items
        } else {
            items.filter { item ->
                getItemTitle(item).contains(searchQuery, ignoreCase = true) ||
                        getItemDescription(item).contains(searchQuery, ignoreCase = true)
            }
        }
    }

    // Clear search when dropdown closes
    LaunchedEffect(isExpanded) {
        if (!isExpanded) {
            searchQuery = ""
        } else if (isExpanded && isSearchEnabled) {
            focusRequester.requestFocus()
        }
    }

    Column(modifier = modifier) {
        // Label
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // Dropdown Button
        Box {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = if (isExpanded) 2.dp else 1.dp,
                        color = when {
                            isError -> MaterialTheme.colorScheme.error
                            isExpanded -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
                        },
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onExpandedChange(!isExpanded) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isExpanded)
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.05f)
                    else
                        MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Leading Icon
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = leadingIcon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Content
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = selectedItem?.let(getItemTitle) ?: placeholder,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (selectedItem != null) FontWeight.Medium else FontWeight.Normal,
                                color = if (selectedItem != null)
                                    MaterialTheme.colorScheme.onSurface
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            selectedItem?.let { item ->
                                val description = getItemDescription(item)
                                if (description.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }

                    // Dropdown Arrow
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Dropdown arrow",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .rotate(rotationAngle)
                            .size(24.dp)
                    )
                }
            }

            // Dropdown Menu
            DropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { onExpandedChange(false) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = Color.Black.copy(alpha = 0.1f)
                    )
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(20.dp)
                    )
            ) {
                Column {
                    // Search Field
                    if (isSearchEnabled) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                BasicTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    modifier = Modifier
                                        .weight(1f)
                                        .focusRequester(focusRequester),
                                    textStyle = TextStyle(
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                        fontWeight = FontWeight.Normal
                                    ),
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        imeAction = ImeAction.Done
                                    ),
                                    singleLine = true,
                                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                    decorationBox = { innerTextField ->
                                        if (searchQuery.isEmpty()) {
                                            Text(
                                                text = searchPlaceholder,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                            )
                                        }
                                        innerTextField()
                                    }
                                )

                                if (searchQuery.isNotEmpty()) {
                                    IconButton(
                                        onClick = { searchQuery = "" },
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear search",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        if (filteredItems.isNotEmpty()) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.05f),
                                thickness = 1.dp
                            )
                        }
                    }

                    // Items List
                    if (filteredItems.isEmpty()) {
                        // No results message
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (searchQuery.isNotEmpty()) "No results found" else "No items available",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Normal
                            )
                        }
                    } else {
                        // Use regular Column with scrolling when items fit maxVisibleItems
                        val itemsToShow = filteredItems.take(maxVisibleItems)
                        val shouldUseScroll = filteredItems.size > maxVisibleItems

                        if (shouldUseScroll) {
                            // Use LazyColumn only when we have many items and need scrolling
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height((maxVisibleItems * 72).dp)
                            ) {
                                itemsIndexed(filteredItems) { index, item ->
                                    DropdownItemContent(
                                        item = item,
                                        index = index,
                                        isLast = index == filteredItems.size - 1,
                                        selectedItem = selectedItem,
                                        getItemTitle = getItemTitle,
                                        getItemDescription = getItemDescription,
                                        getItemId = getItemId,
                                        onItemSelected = onItemSelected,
                                        onExpandedChange = onExpandedChange
                                    )
                                }
                            }
                        } else {
                            // Use regular Column for small lists to avoid intrinsic measurement issues
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                itemsToShow.forEachIndexed { index, item ->
                                    DropdownItemContent(
                                        item = item,
                                        index = index,
                                        isLast = index == itemsToShow.size - 1,
                                        selectedItem = selectedItem,
                                        getItemTitle = getItemTitle,
                                        getItemDescription = getItemDescription,
                                        getItemId = getItemId,
                                        onItemSelected = onItemSelected,
                                        onExpandedChange = onExpandedChange
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Error Message
        if (isError && errorMessage.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
private fun <T> DropdownItemContent(
    item: T,
    index: Int,
    isLast: Boolean,
    selectedItem: T?,
    getItemTitle: (T) -> String,
    getItemDescription: (T) -> String,
    getItemId: (T) -> Any,
    onItemSelected: (T) -> Unit,
    onExpandedChange: (Boolean) -> Unit
) {
    val isSelected = selectedItem?.let(getItemId) == getItemId(item)

    DropdownMenuItem(
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = getItemTitle(item),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )

                    val description = getItemDescription(item)
                    if (description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                alpha = if (isSelected) 0.8f else 0.6f
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (isSelected) {
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        },
        onClick = {
            onItemSelected(item)
            onExpandedChange(false)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = MenuDefaults.itemColors(
            textColor = MaterialTheme.colorScheme.onSurface,
            disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        )
    )

    if (!isLast) {
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
            thickness = 1.dp
        )
    }
}

@Composable
fun <T> ReusableDropdown(
    modifier: Modifier = Modifier,
    label: String,
    items: List<T>,
    selectedItem: T?,
    placeholder: String,
    leadingIcon: ImageVector,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onItemSelected: (T) -> Unit,
    getItemTitle: (T) -> String,
    getItemDescription: (T) -> String = { "" },
    getItemId: (T) -> Any,
    isError: Boolean = false,
    errorMessage: String = ""
) {
    SearchableDropdown(
        modifier = modifier,
        label = label,
        items = items,
        selectedItem = selectedItem,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        isExpanded = isExpanded,
        onExpandedChange = onExpandedChange,
        onItemSelected = onItemSelected,
        getItemTitle = getItemTitle,
        getItemDescription = getItemDescription,
        getItemId = getItemId,
        isError = isError,
        errorMessage = errorMessage,
        isSearchEnabled = false
    )
}