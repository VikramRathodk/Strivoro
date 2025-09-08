package com.devvikram.striveo.firebase.models


import com.google.firebase.firestore.PropertyName

data class FirebaseModule(

    @PropertyName("moduleId")
    val moduleId: String = "",

    @PropertyName("projectId")
    val projectId: String = "",   // Reference to Project

    @PropertyName("title")
    val title: String = "",

    @PropertyName("description")
    val description: String = "",

    @PropertyName("status")
    val status: String = "",      // "pending", "in_progress", "completed"

    @PropertyName("priority")
    val priority: Int = 0,        // 0 = low, 1 = medium, 2 = high

    @PropertyName("createdBy")
    val createdBy: String = "",   // userId of creator

    @PropertyName("createdAt")
    val createdAt: Long = System.currentTimeMillis(),

    @PropertyName("lastModifiedAt")
    val lastModifiedAt: Long = System.currentTimeMillis(),

    @PropertyName("dueDate")
    val dueDate: Long? = null,    // nullable deadline

    @PropertyName("progress")
    val progress: Int = 0         // percentage 0–100
)
