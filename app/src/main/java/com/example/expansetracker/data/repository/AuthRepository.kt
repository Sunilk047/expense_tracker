package com.example.expansetracker.data.repository

import android.util.Log
import com.example.expansetracker.data.local.User
import com.example.expansetracker.data.local.UserSessionManager
import com.example.expansetracker.data.remote.SupabaseApi
import com.example.expansetracker.data.remote.SupabaseApi.ApiResponse
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val sessionManager: UserSessionManager
) {

    suspend fun login(email: String, password: String): ApiResponse {
        val response = SupabaseApi.login(email, password)

        // Optional: save session if login returns tokens later
        if (response.user != null) {
            Log.d("SESSION", "LOGINCHECK = ${response.user}")
            val user = User(
                id = response.user.id,
                email = response.user.email,
                fullName = response.user.full_name,
                phone = response.user.phone,
                isVerified = response.user.is_verified
            )
            sessionManager.saveUser(
                user = user,
                accessToken = ""
            )
        }

        return response
    }

    fun getLoggedInUser(): User? = sessionManager.getUser()

    fun isLoggedIn(): Boolean = sessionManager.isLoggedIn()

    suspend fun signup(
        email: String,
        password: String,
        fullName: String,
        phone: String
    ): ApiResponse {
        return SupabaseApi.signup(email, password, fullName, phone)
    }

    suspend fun verifyOtp(email: String, otp: String): ApiResponse {
        val response = SupabaseApi.verifyOtp(email, otp)

        if (response.access_token != null && response.user != null) {
//            saveUserSession(response)
        }

        return response
    }

    suspend fun resendOtp(email: String): ApiResponse {
        return SupabaseApi.resendOtp(email)
    }

    suspend fun profileUpdate(
        email: String,
        fullName: String,
        phone: String,
        userId: String
    ): ApiResponse {
        val response = SupabaseApi.updateProfile(email, fullName, phone, userId)

        if (response.user != null) {
            val user = User(
                id = response.user.id,
                email = response.user.email,
                fullName = response.user.full_name,
                phone = response.user.phone,
                isVerified = response.user.is_verified
            )
            sessionManager.saveUser(
                user = user,
                accessToken = ""
            )
        }

        return response
    }

    suspend fun logout(): ApiResponse {
        sessionManager.clear()
        return SupabaseApi.logout()
    }
}
