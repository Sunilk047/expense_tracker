package com.example.expansetracker.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.Serializable

@Singleton
class UserSessionManager @Inject constructor(
    @ApplicationContext context: Context
) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    private val gson = Gson()

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_JSON = "user_json"
        private const val KEY_ACCESS_TOKEN = "access_token"
    }

    /** Save logged-in user session */
    fun saveUser(user: User, accessToken: String) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USER_JSON, gson.toJson(user))
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .apply()
        Log.d("SESSION", "User saved: ${user.email}")

    }

    /** Get logged-in user */
    fun getUser(): User? {
        val json = prefs.getString(KEY_USER_JSON, null) ?: return null
        return try {
            gson.fromJson(json, User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    /** Get access token */
    fun getAccessToken(): String? =
        prefs.getString(KEY_ACCESS_TOKEN, null)

    /** Check login state */
    fun isLoggedIn(): Boolean {
        val loggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        Log.d("SESSION", "isLoggedIn = $loggedIn")
        return loggedIn
    }


    /** Clear session on logout */
    fun clear() {
        prefs.edit().clear().apply()
    }
}


@Serializable
data class User(
    val id: String,
    val email: String,
    val fullName: String?,
    val phone: String?,
    val isVerified: Boolean,

)
