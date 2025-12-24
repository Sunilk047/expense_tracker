package com.example.expansetracker.viewmodel

import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expansetracker.data.repository.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository,
) : ViewModel() {

    var name by mutableStateOf("")
        private set
    var email by mutableStateOf("")
        private set
    var phone by mutableStateOf("")
        private set
    var photoUrl by mutableStateOf<String?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            val user = repository.getProfile()
            name = user.name
            email = firebaseUser?.email ?: user.email
            phone = user.phone ?: ""
            photoUrl = user.photoUrl
        }
    }

    fun onNameChange(v: String) {
        name = v
    }

    fun onPhoneChange(v: String) {
        phone = v
    }

//    fun uploadImage(
//        uri: Uri,
//        onError: (String) -> Unit
//    ) {
//        viewModelScope.launch {
//            try {
//                isLoading = true
//                photoUrl = repository.uploadProfileImage(uri)
//
//                // 🔥 Update Firestore
//                repository.updateProfile(name, phone, photoUrl)
//
//                // 🔥 Sync globally
//                authViewModel.updateUser(name, photoUrl)
//
//            } catch (e: Exception) {
//                onError(e.message ?: "Upload failed")
//            } finally {
//                isLoading = false
//            }
//        }
//    }

    fun updateProfile(
        authViewModel: AuthViewModel, // pass AuthViewModel
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                isLoading = true
                repository.updateProfile(name, phone, photoUrl)

//                 🔥 Update global state
                authViewModel.updateUser(name, phone, photoUrl)

                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Update failed")
            } finally {
                isLoading = false
            }
        }
    }
}

