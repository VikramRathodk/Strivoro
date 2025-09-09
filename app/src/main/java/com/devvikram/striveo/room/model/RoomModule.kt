package com.devvikram.striveo.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "modules")
data class RoomModule(

    @PrimaryKey
    @ColumnInfo(name = "module_id")
    val moduleId: String = "",

    @ColumnInfo(name = "project_id")
    val projectId: String = "",   // Foreign key reference to Project

    @ColumnInfo(name = "title")
    val title: String = "",

    @ColumnInfo(name = "description")
    val description: String = "",

    @ColumnInfo(name = "status")
    val status: String = "",      // e.g., "pending", "in_progress", "completed"

    @ColumnInfo(name = "priority")
    val priority: Int = 0,        // 0 = low, 1 = medium, 2 = high

    @ColumnInfo(name = "created_by")
    val createdBy: String = "",   // userId of creator

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "last_modified_at")
    val lastModifiedAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "due_date")
    val dueDate: Long? = null,    // deadline timestamp (nullable)

    @ColumnInfo(name = "progress")
    val progress: Int = 0         // percentage 0–100
)