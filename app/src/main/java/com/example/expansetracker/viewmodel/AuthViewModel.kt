package com.example.expansetracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expansetracker.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    var isLoading = false
        private set

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                isLoading = true
                repository.login(email, password)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Login failed")
            } finally {
                isLoading = false
            }
        }
    }

    fun signup(
        email: String,
        name: String,
        phone: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                isLoading = true
                repository.signup(email, password,name,phone)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Signup failed")
            } finally {
                isLoading = false
            }
        }
    }

//    fun resetPassword(
//        email: String,
//        onSuccess: () -> Unit,
//        onError: (String) -> Unit
//    ) {
//        viewModelScope.launch {
//            try {
//                repository.resetPassword(email)
//                onSuccess()
//            } catch (e: Exception) {
//                onError(e.message ?: "Reset failed")
//            }
//        }
//    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.logout()
            onComplete()
        }
    }
}
