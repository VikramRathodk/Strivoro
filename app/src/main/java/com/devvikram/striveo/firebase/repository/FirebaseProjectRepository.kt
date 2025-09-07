package com.devvikram.striveo.firebase.repository

import com.devvikram.striveo.config.constants.App
import com.devvikram.striveo.firebase.models.FirebaseProject
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class FirebaseProjectRepository @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore
) {

    private val projectsCollection = firebaseFirestore.collection(App.FIREBASE_COLLECTION_PROJECTS)

    // Add new project to Firestore
    fun addProject(
        project: FirebaseProject,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            projectsCollection.document(project.projectId).set(project)
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    onFailure(e)
                }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    // update project fields
    fun updateField(
        projectId: String,
        fields : Map<String, Any>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ){
        try {
            projectsCollection.document(projectId).update(fields)
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    onFailure(e)
                }
        } catch (e: Exception) {
            onFailure(e)
        }
    }




}