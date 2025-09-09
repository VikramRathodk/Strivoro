package com.devvikram.striveo.firebase.repository

import android.util.Log
import com.devvikram.striveo.config.constants.App
import com.devvikram.striveo.firebase.models.MyFirebaseUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject


class FirebaseUserRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    private val TAG = "FirebaseUserRepository"

    private val userCollection = firestore.collection(App.FIREBASE_COLLECTION_USERS)

    // insert user to firebase
    fun insertUserToFirebase(
        user: MyFirebaseUser,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        Log.d(TAG, "Attempting to insert user to Firebase: $user")

        try {
            // Check if userId is valid
            if (user.userId.isBlank()) {
                Log.e(TAG, "insertUserToFirebase: userId is blank. Aborting insert.")
                return
            }

            userCollection
                .document(user.userId)
                .set(user)
                .addOnSuccessListener {
                    Log.d(TAG, "User inserted to Firestore successfully")
                    onSuccess()
                }
                .addOnFailureListener { exception ->
                    Log.e(
                        TAG,
                        "Failed to insert user to Firestore: ${exception.message}",
                        exception
                    )
                    onFailure(exception)
                }

        } catch (e: Exception) {
            Log.e(TAG, "Exception during insertUserToFirebase: ${e.message}", e)
            onFailure(e)
        }
    }

    suspend fun updateField(
        userId: String,
        field: Map<String, Any>
    ) {
        try {
            userCollection
                .document(userId)
                .update(field).addOnSuccessListener {
                    println("User inserted to firebase")
                }.addOnFailureListener {
                    println("User not inserted to firebase")
                }

        } catch (e: Exception) {
            e.printStackTrace()

        }

    }

    fun isUserAuthenticatedInFirebase(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun signUpUserToFirebase(
        mapToMyFirebaseUser: MyFirebaseUser,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        firebaseAuth.createUserWithEmailAndPassword(
            mapToMyFirebaseUser.email,
            mapToMyFirebaseUser.password
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                println("User signed up successfully with email: ${mapToMyFirebaseUser.email}")
                onSuccess()
            } else {
                val exception = task.exception
                exception?.printStackTrace()
                onFailure(exception ?: Exception("Unknown error occurred during signup"))
            }
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    fun signInWithEmailAndPassword(
        email: String,
        password: String,
        onSuccess: (FirebaseUser) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val user = authResult.user
                if (user != null) {
                    println("signInWithEmailAndPassword: success for $email")
                    onSuccess(user)
                } else {
                    onFailure(Exception("Authentication succeeded, but user is null"))
                }
            }
            .addOnFailureListener { exception ->
                println("signInWithEmailAndPassword: failed for $email, reason = ${exception.message}")
                onFailure(exception)
            }
    }

    fun signIn(
        email: String,
        password: String,
        onSuccess: (MyFirebaseUser) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        userCollection.whereEqualTo("email", email)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    val user = document.toObject(MyFirebaseUser::class.java)
                    if (user != null) {
                        println("User retrieved from Firestore: ${user.email}")
                        onSuccess(user)
                    } else {
                        onFailure(Exception("User data is null or malformed"))
                    }
                } else {
                    onFailure(Exception("No user found with this email"))
                }
            }
            .addOnFailureListener { exception ->
                println("Failed to retrieve user: ${exception.message}")
                onFailure(exception)
            }
    }

    fun updateUserLastLogin(userId: String, currentTime: Long) {
        userCollection.document(userId)
            .update("lastLoginAt", currentTime)
            .addOnSuccessListener {
                println("User's last login time updated successfully")
            }
            .addOnFailureListener { exception ->
                println("Failed to update user's last login time: ${exception.message}")
            }
    }


}