package com.devvikram.striveo.firebase.repository

import com.devvikram.striveo.config.constants.App
import com.devvikram.striveo.config.constants.LoginPreference
import com.devvikram.striveo.firebase.models.FirebaseModule
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseModuleRepository @Inject constructor(
    private val loginPreference: LoginPreference,
    private val firebaseFirestore: FirebaseFirestore
) {

    private val modulesCollection = firebaseFirestore.collection(App.FIREBASE_COLLECTION_MODULES)

    fun addOrUpdateModule(
        module: FirebaseModule,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            modulesCollection.document(module.moduleId)
                .set(module)
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener {
                    onFailure(it)
                }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    fun updateFields (
        moduleId: String,
        fields: Map<String, Any>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ){
        try {
            modulesCollection.document(moduleId)
                .update(fields)
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener {
                    onFailure(it)
                }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

}