//package com.example.expansetracker.ui.splash
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.expansetracker.data.local.UserSessionManager
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class SplashViewModel @Inject constructor(
//    private val sessionManager: UserSessionManager
//) : ViewModel() {
//
//    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
//    val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn
//
//    init {
//        checkUserSession()
//    }
//
//    private fun checkUserSession() {
//        viewModelScope.launch {
//            val loggedIn = sessionManager.isUserLoggedIn()
//            _isLoggedIn.value = loggedIn
//        }
//    }
//}
