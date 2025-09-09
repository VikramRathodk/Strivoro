package com.devvikram.striveo.firebase.models

import com.google.firebase.firestore.PropertyName


data class FirebaseProject(
    @PropertyName("projectId")
    val projectId: String = "",

    @PropertyName("projectName")
    val projectName: String = "",

    @PropertyName("description")
    val description: String = "",

    @PropertyName("createdBy")
    val createdBy: String = "",

    @PropertyName("createdAt")
    val createdAt: Long = System.currentTimeMillis(),

    @PropertyName("lastModifiedAt")
    val lastModifiedAt: Long = System.currentTimeMillis(),

    @PropertyName("status")
    val status: String = "active",
)
