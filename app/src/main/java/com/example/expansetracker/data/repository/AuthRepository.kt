package com.example.expansetracker.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    /* ---------------- LOGIN ---------------- */
    fun login(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { exception ->
                onResult(false, exception.message)
            }
    }

    /* ---------------- SIGNUP ---------------- */
    fun signup(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { exception ->
                onResult(false, exception.message)
            }
    }

    /* ---------------- PASSWORD RESET ---------------- */
    fun resetPassword(
        email: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { exception ->
                onResult(false, exception.message)
            }
    }

    /* ---------------- SESSION ---------------- */
    fun isLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    fun getCurrentUser() = firebaseAuth.currentUser

    fun getUserEmail(): String? {
        return firebaseAuth.currentUser?.email
    }

    fun getUserName(): String {
        return firebaseAuth.currentUser?.displayName ?: "User"
    }


    fun updateProfile(
        fullName: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val user = firebaseAuth.currentUser
        if (user == null) {
            onResult(false, "User not logged in")
            return
        }

        val updates = userProfileChangeRequest {
            displayName = fullName
        }

        user.updateProfile(updates)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.message) }
    }


    /* ---------------- LOGOUT ---------------- */
    fun logout() {
        firebaseAuth.signOut()
    }
}
