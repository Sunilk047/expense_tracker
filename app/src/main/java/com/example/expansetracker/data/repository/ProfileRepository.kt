package com.example.expansetracker.data.repository

import android.net.Uri
import com.example.expansetracker.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
//    private val storage: FirebaseStorage
) {

    //    suspend fun uploadProfileImage(uri: Uri): String {
//        val uid = auth.currentUser?.uid ?: throw Exception("Not logged in")
//
//        val ref = storage.reference
//            .child("profile/$uid.jpg")
//
//        ref.putFile(uri).await()
//
//        return ref.downloadUrl.await().toString()
//    }
//
    suspend fun updateProfile(
        name: String,
        phone: String?,
        photoUrl: String?
    ) {
        val uid = auth.currentUser?.uid ?: throw Exception("Not logged in")

        firestore.collection("users")
            .document(uid)
            .set(
                mapOf(
                    "name" to name,
                    "phone" to phone,
                    "photoUrl" to photoUrl
                ),
                SetOptions.merge() // <--- important!
            )
            .await()
    }

    suspend fun getProfile(): UserProfile {
        val uid = auth.currentUser?.uid ?: throw Exception("Not logged in")
        val doc = firestore.collection("users").document(uid).get().await()
//        return doc.toObject(UserProfile::class.java)!!
        return doc.toObject(UserProfile::class.java)
            ?: UserProfile(
                uid = uid,
                email = auth.currentUser?.email.orEmpty()
            )
    }
}
