package com.devvikram.striveo.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class RoomProject(
    @PrimaryKey
    @ColumnInfo(name = "project_id")
    val projectId: String = "",

    @ColumnInfo(name = "project_name")
    val projectName: String = "",

    @ColumnInfo(name = "project_description")
    val description: String = "",

    @ColumnInfo(name = "created_by")
    val createdBy: String = "",

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "last_modified_at")
    val lastModifiedAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "status")
    val status: String = "active"
)