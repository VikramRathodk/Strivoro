package com.devvikram.striveo.firebase.models

import com.google.firebase.firestore.PropertyName

data class FirebaseTask(
    @PropertyName("taskId")
    val taskId: String = "",
    @PropertyName("title")
    val title: String = "",
    @PropertyName("description")
    val description: String = "",
    @PropertyName("category")
    val category: String = "",
    @PropertyName("priority")
    val priority: String = "",
    @PropertyName("estimatedTime")
    val estimatedTime: String = "",
    @PropertyName("dueDate")
    val dueDate: String = "",
    @PropertyName("isCompleted")
    val isCompleted: Boolean = false,
    @PropertyName("tags")
    val tags: List<String> = emptyList(),
    @PropertyName("createdAt")
    val createdAt: Long = System.currentTimeMillis(),
    @PropertyName("lastModifiedAt")
    val lastModifiedAt: Long = System.currentTimeMillis(),

    @PropertyName("createdBy")
    val createdBy: String = ""
)
