package com.devvikram.striveo.ui.screens.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devvikram.striveo.config.enums.TaskFilter
import com.devvikram.striveo.config.enums.TaskPriority
import com.devvikram.striveo.room.model.RoomTask
import com.devvikram.striveo.room.repository.RoomTaskRepository
import com.devvikram.striveo.ui.TaskStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.emptyList


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val roomTaskRepository: RoomTaskRepository
) : ViewModel() {

    private val _tasks = MutableStateFlow(emptyList<RoomTask>())
    val tasks: StateFlow<List<RoomTask>> = _tasks.asStateFlow()

    private val _selectedFilter = MutableStateFlow(TaskFilter.TODAY)
    val selectedFilter: StateFlow<TaskFilter> = _selectedFilter.asStateFlow()


    val filteredTasks: StateFlow<List<RoomTask>> = combine(
        roomTaskRepository.getAllTasks(),
        _selectedFilter
    ) { taskList, filter ->
        when (filter) {
            TaskFilter.ALL -> taskList
            TaskFilter.TODAY -> taskList.filter {
                it.dueDate == "Today" || it.dueDate.contains("AM") || it.dueDate.contains("PM")
            }
            TaskFilter.UPCOMING -> taskList.filter {
                it.dueDate == "Tomorrow" || it.dueDate == "This Week"
            }
            TaskFilter.COMPLETED -> taskList.filter { it.isCompleted }
            TaskFilter.OVERDUE -> taskList.filter {
                !it.isCompleted && it.dueDate == "Yesterday"
            }
        }
    }.stateFlow(viewModelScope, emptyList())

    val taskStats: StateFlow<TaskStats> = _tasks.map { taskList ->
        TaskStats(
            totalTasks = taskList.size,
            completedTasks = taskList.count { it.isCompleted },
            pendingTasks = taskList.count { !it.isCompleted },
            overdueTasks = taskList.count { !it.isCompleted && it.dueDate == "Yesterday" },
            focusTime = "4.2h",
            productivity = 87,
            weeklyCompletion = 85
        )
    }.stateFlow(viewModelScope, TaskStats(0, 0, 0, 0, "0h", 0, 0))

    val greeting: String
        get() {
            val currentTime = SimpleDateFormat("HH", Locale.getDefault()).format(Date()).toInt()
            return when {
                currentTime < 12 -> "Good Morning"
                currentTime < 17 -> "Good Afternoon"
                else -> "Good Evening"
            }
        }

    fun toggleTask(taskId: String) {
        viewModelScope.launch {
            _tasks.value = _tasks.value.map { task ->
                if (task.taskId == taskId) {
                    task.copy(isCompleted = !task.isCompleted)
                } else {
                    task
                }
            }
        }
    }

    fun setFilter(filter: TaskFilter) {
        _selectedFilter.value = filter
    }


    private fun getSampleTasks(): List<RoomTask> {
        return listOf(
            RoomTask("1", "Complete project presentation", "Prepare slides for quarterly business review meeting", "Work", TaskPriority.HIGH.name, "3h", "Today", true, listOf("presentation", "urgent", "meeting")),
            RoomTask("2", "Buy groceries for dinner", "Get ingredients for pasta and salad", "Personal", TaskPriority.MEDIUM.name, "1h", "Today", true, listOf("shopping", "food")),
            RoomTask("3", "Morning workout routine", "30 minutes cardio + strength training", "Health", TaskPriority.HIGH.name, "45min", "Today", true, listOf("fitness", "routine", "morning")),
            RoomTask("4", "Review code changes", "Check pull requests from team members", "Work", TaskPriority.MEDIUM.name, "2h", "Today", false, listOf("code-review", "development")),
            RoomTask("5", "Call dentist for appointment", "Schedule routine cleaning appointment", "Health", TaskPriority.LOW.name, "15min", "Tomorrow", false, listOf("appointment", "health")),
            RoomTask("6", "Read chapter 5 of Kotlin book", "Study coroutines and async programming", "Learning", TaskPriority.MEDIUM.name, "1.5h", "Tomorrow", false, listOf("programming", "study", "kotlin")),
            RoomTask("7", "Submit expense report", "Upload receipts from last week's business trip", "Work", TaskPriority.HIGH.name, "30min", "Yesterday", false, listOf("expenses", "finance", "overdue")),
            RoomTask("8", "Plan weekend hiking trip", "Research trails and book accommodation", "Personal", TaskPriority.LOW.name, "2h", "This Week", false, listOf("travel", "outdoor", "planning")),
            RoomTask("9", "Update LinkedIn profile", "Add recent project achievements and skills", "Personal", TaskPriority.LOW.name, "45min", "This Week", true, listOf("career", "networking", "profile")),
            RoomTask("10", "Team standup meeting", "Daily sync with development team", "Work", TaskPriority.MEDIUM.name, "30min", "Today", true, listOf("meeting", "standup", "team")),
            RoomTask("11", "Practice guitar", "Work on new song chord progressions", "Personal", TaskPriority.LOW.name, "1h", "Today", false, listOf("music", "hobby", "practice")),
            RoomTask("12", "Prepare tax documents", "Gather W2s and receipts for tax filing", "Personal", TaskPriority.HIGH.name, "2.5h", "This Week", false, listOf("taxes", "documents", "finance")),
            RoomTask("13", "Organize closet", "Sort clothes and donate unused items", "Personal", TaskPriority.MEDIUM.name, "2h", "This Month", false, listOf("cleaning", "organization")),
            RoomTask("14", "Client follow-up emails", "Respond to open queries and schedule calls", "Work", TaskPriority.HIGH.name, "1h", "Tomorrow", false, listOf("email", "client", "communication")),
            RoomTask("15", "Yoga session", "Attend online yoga class", "Health", TaskPriority.MEDIUM.name, "1h", "Today", false, listOf("yoga", "wellness")),
            RoomTask("16", "Write blog post", "Topic: Productivity hacks with AI tools", "Learning", TaskPriority.HIGH.name, "2h", "This Week", false, listOf("writing", "blog", "productivity")),
            RoomTask("17", "Paint bedroom", "Choose colors and repaint walls", "Personal", TaskPriority.MEDIUM.name, "4h", "Weekend", false, listOf("home", "DIY")),
            RoomTask("18", "Watch design tutorial", "Figma advanced tips", "Learning", TaskPriority.LOW.name, "1h", "This Week", true, listOf("design", "figma")),
            RoomTask("19", "Laundry", "Wash and fold clothes", "Personal", TaskPriority.LOW.name, "1.5h", "Today", true, listOf("chores", "home")),
            RoomTask("20", "Backup project files", "Upload to cloud storage", "Work", TaskPriority.HIGH.name, "30min", "Today", false, listOf("backup", "project", "cloud")),
            RoomTask("21", "Plan content calendar", "Outline social posts for next 2 weeks", "Work", TaskPriority.MEDIUM.name, "2h", "Tomorrow", false, listOf("content", "planning")),
            RoomTask("22", "Refactor login screen", "Clean architecture for login flow", "Work", TaskPriority.HIGH.name, "2h", "Today", false, listOf("refactor", "login")),
            RoomTask("23", "Walk the dog", "Evening walk at the park", "Personal", TaskPriority.LOW.name, "30min", "Today", false, listOf("pet", "exercise")),
            RoomTask("24", "Doctor appointment", "Annual check-up", "Health", TaskPriority.MEDIUM.name, "1h", "Next Week", false, listOf("checkup", "health")),
            RoomTask("25", "Finish puzzle", "1000 piece jigsaw", "Personal", TaskPriority.LOW.name, "3h", "This Week", false, listOf("hobby", "brain")),
            RoomTask("26", "Clean kitchen", "Deep clean and organize pantry", "Personal", TaskPriority.MEDIUM.name, "2h", "This Week", true, listOf("cleaning", "kitchen")),
            RoomTask("27", "Design portfolio update", "Add latest projects", "Work", TaskPriority.MEDIUM.name, "1.5h", "Today", false, listOf("portfolio", "design")),
            RoomTask("28", "Research investment options", "Compare mutual funds", "Personal", TaskPriority.MEDIUM.name, "1h", "This Month", true, listOf("finance", "investments")),
            RoomTask("29", "Volunteer call", "NGO coordination call", "Personal", TaskPriority.MEDIUM.name, "1h", "Tomorrow", true, listOf("volunteer", "call")),
            RoomTask("30", "Install latest OS update", "Device maintenance", "Personal", TaskPriority.LOW.name, "45min", "This Week", false, listOf("system", "update")),
            RoomTask("31", "Schedule team 1-on-1s", "Plan for feedback sessions", "Work", TaskPriority.MEDIUM.name, "1h", "Next Week", true, listOf("feedback", "team")),

        )
    }

}

// Extension function to create StateFlow from Flow
private fun <T> kotlinx.coroutines.flow.Flow<T>.stateFlow(
    scope: kotlinx.coroutines.CoroutineScope,
    initialValue: T
): StateFlow<T> {
    val stateFlow = MutableStateFlow(initialValue)
    scope.launch {
        collect { stateFlow.value = it }
    }
    return stateFlow.asStateFlow()
}