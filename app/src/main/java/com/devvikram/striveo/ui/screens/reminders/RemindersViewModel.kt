package com.devvikram.striveo.ui.screens.reminders

// RemindersViewModel.kt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class RemindersViewModel @Inject constructor() : ViewModel() {

    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders: StateFlow<List<Reminder>> = _reminders.asStateFlow()

    private val _selectedFilter = MutableStateFlow(ReminderFilter.ALL)
    val selectedFilter: StateFlow<ReminderFilter> = _selectedFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()

    val filteredReminders = combine(
        _reminders,
        _selectedFilter,
        _searchQuery
    ) { reminders, filter, query ->
        var filtered = when (filter) {
            ReminderFilter.ALL -> reminders
            ReminderFilter.TODAY -> reminders.filter {
                it.dateTime.toLocalDate() == LocalDateTime.now().toLocalDate()
            }
            ReminderFilter.UPCOMING -> reminders.filter {
                it.dateTime.isAfter(LocalDateTime.now()) && !it.isCompleted
            }
            ReminderFilter.COMPLETED -> reminders.filter { it.isCompleted }
            ReminderFilter.HIGH_PRIORITY -> reminders.filter {
                it.priority == ReminderPriority.HIGH && !it.isCompleted
            }
        }

        if (query.isNotBlank()) {
            filtered = filtered.filter { reminder ->
                reminder.title.contains(query, ignoreCase = true) ||
                        reminder.description.contains(query, ignoreCase = true) ||
                        reminder.tags.any { it.contains(query, ignoreCase = true) }
            }
        }

        filtered.sortedBy { it.dateTime }
    }

    init {
        loadSampleReminders()
    }

    fun setFilter(filter: ReminderFilter) {
        _selectedFilter.value = filter
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun showAddDialog() {
        _showAddDialog.value = true
    }

    fun hideAddDialog() {
        _showAddDialog.value = false
    }

    fun addReminder(reminder: Reminder) {
        _reminders.value = _reminders.value + reminder
    }

    fun toggleReminderCompletion(reminderId: String) {
        _reminders.value = _reminders.value.map { reminder ->
            if (reminder.id == reminderId) {
                reminder.copy(isCompleted = !reminder.isCompleted)
            } else {
                reminder
            }
        }
    }

    fun deleteReminder(reminderId: String) {
        _reminders.value = _reminders.value.filter { it.id != reminderId }
    }

    fun getReminderStats(): ReminderStats {
        val allReminders = _reminders.value
        return ReminderStats(
            totalReminders = allReminders.size,
            completedReminders = allReminders.count { it.isCompleted },
            pendingReminders = allReminders.count { !it.isCompleted },
            todayReminders = allReminders.count {
                it.dateTime.toLocalDate() == LocalDateTime.now().toLocalDate()
            },
            highPriorityReminders = allReminders.count {
                it.priority == ReminderPriority.HIGH && !it.isCompleted
            }
        )
    }

    private fun loadSampleReminders() {
        viewModelScope.launch {
            val sampleReminders = listOf(
                Reminder(
                    id = "1",
                    title = "Doctor Appointment",
                    description = "Annual health checkup with Dr. Smith",
                    dateTime = LocalDateTime.now().plusDays(2).withHour(10).withMinute(0),
                    priority = ReminderPriority.HIGH,
                    category = ReminderCategory.HEALTH,
                    tags = listOf("health", "appointment")
                ),
                Reminder(
                    id = "2",
                    title = "Pay Electricity Bill",
                    description = "Monthly electricity bill due",
                    dateTime = LocalDateTime.now().plusDays(3).withHour(18).withMinute(0),
                    priority = ReminderPriority.MEDIUM,
                    category = ReminderCategory.FINANCE,
                    isRecurring = true,
                    recurrenceType = RecurrenceType.MONTHLY,
                    tags = listOf("bills", "utilities")
                ),
                Reminder(
                    id = "3",
                    title = "Team Meeting",
                    description = "Weekly team sync and project updates",
                    dateTime = LocalDateTime.now().plusHours(2),
                    priority = ReminderPriority.HIGH,
                    category = ReminderCategory.WORK,
                    isRecurring = true,
                    recurrenceType = RecurrenceType.WEEKLY,
                    tags = listOf("meeting", "work", "team")
                ),
                Reminder(
                    id = "4",
                    title = "Call Mom",
                    description = "Weekly check-in call with family",
                    dateTime = LocalDateTime.now().plusDays(1).withHour(19).withMinute(0),
                    priority = ReminderPriority.MEDIUM,
                    category = ReminderCategory.PERSONAL,
                    tags = listOf("family", "call")
                ),
                Reminder(
                    id = "5",
                    title = "Submit Report",
                    description = "Monthly project status report",
                    dateTime = LocalDateTime.now().minusHours(2),
                    priority = ReminderPriority.HIGH,
                    category = ReminderCategory.WORK,
                    isCompleted = true,
                    tags = listOf("report", "deadline")
                )
            )
            _reminders.value = sampleReminders
        }
    }
}