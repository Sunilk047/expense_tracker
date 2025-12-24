package com.example.expansetracker.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expansetracker.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    /* ---------------- UI STATE ---------------- */
    var isLoading by mutableStateOf(false)
        private set

    /* ---------------- LOGIN ---------------- */
    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        isLoading = true
        authRepository.login(email, password) { success, error ->
            isLoading = false
            if (success) {
                onSuccess()
            } else {
                onError(error ?: "Login failed")
            }
        }
    }

    /* ---------------- SIGNUP ---------------- */
    fun signup(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        isLoading = true
        authRepository.signup(email, password) { success, error ->
            isLoading = false
            if (success) {
                onSuccess()
            } else {
                onError(error ?: "Signup failed")
            }
        }
    }

    /* ---------------- RESET PASSWORD ---------------- */
    fun resetPassword(
        email: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        isLoading = true
        authRepository.resetPassword(email) { success, error ->
            isLoading = false
            if (success) {
                onSuccess()
            } else {
                onError(error ?: "Failed to send reset email")
            }
        }
    }

    /* ---------------- SESSION ---------------- */
    fun isLoggedIn(): Boolean {
        return authRepository.isLoggedIn()
    }

    fun logout() {
        authRepository.logout()
    }

    /* ---------------- OTP (UI PLACEHOLDER) ---------------- */
    /**
     * Firebase Email/Password auth does NOT use OTP.
     * This is kept only because your UI has an OTP screen.
     */
    fun verifyOtp(
        otp: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (otp.length == 6) {
            onSuccess()
        } else {
            onError("Invalid OTP")
        }
    }

    val userEmail: String?
        get() = authRepository.getUserEmail()

    var userName by mutableStateOf(FirebaseAuth.getInstance().currentUser?.displayName ?: "User")
        private set

    var userMobile by mutableStateOf(FirebaseAuth.getInstance().currentUser?.phoneNumber ?: "")
        private set

    init {
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            val snapshot = FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .await()
            userName = snapshot.getString("name") ?: userName
            userMobile = snapshot.getString("phone") ?: userMobile
        }
    }


    var userPhoto by mutableStateOf<String?>(null)
        private set

    fun updateUser(name: String, phone: String, photo: String?) {
        userName = name
        userMobile = phone
        userPhoto = photo
    }
}
