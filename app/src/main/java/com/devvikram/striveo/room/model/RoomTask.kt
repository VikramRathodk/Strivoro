package com.devvikram.striveo.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.devvikram.striveo.config.enums.TaskStatus

@Entity(tableName = "tasks")
data class RoomTask(
    @PrimaryKey
    val taskId: String,
    val title: String,
    val description: String = "",
    val category: String,
    val priority: String,
    val estimatedTime: String,
    val status: TaskStatus = TaskStatus.NOT_STARTED,
    val dueDate: String,
    val isCompleted: Boolean,
    val tags: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val lastModifiedAt: Long = System.currentTimeMillis(),
    val createdBy: String = "", // Reference to the user who created the task

)
