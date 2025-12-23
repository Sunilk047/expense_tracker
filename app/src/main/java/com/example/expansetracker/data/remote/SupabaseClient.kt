package com.example.expansetracker.data.remote

import android.util.Log
import com.example.expansetracker.data.local.Expense
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object SupabaseApi {

    private const val SUPABASE_URL = "https://jgcikmlznuurizoxywyn.supabase.co"
    private const val SUPABASE_ANON_KEY =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImpnY2lrbWx6bnV1cml6b3h5d3luIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTQ5ODA2NzQsImV4cCI6MjA3MDU1NjY3NH0.rVVPMaaljYTqmb-bJYnFN9MJYZvvwalyxDilC2666F8"

    private var accessToken: String? = null

    val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }

        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
        }
    }

    @Serializable
    data class ApiResponse(
        val message: String? = null,
        val error: String? = null,
        val access_token: String? = null,
        val refresh_token: String? = null,
        val user: UserDto? = null
    )

    @Serializable
    data class UserDto(
        val id: String,
        val email: String,
        val full_name: String? = null,
        val phone: String? = null,
        val is_verified: Boolean = false,
        val created_at: String? = null
    )

    data class ExpenseListResponse(
        val expenses: List<Expense> = emptyList(),
        val message: String? = null,
        val error: String? = null
    )


    private suspend fun postRequest(url: String, bodyMap: Any?): String {
        println("➡️ Request to $url with body: $bodyMap")
        val responseText = client.post(url) {
            headers {
                append("apikey", SUPABASE_ANON_KEY)
                append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                append(HttpHeaders.Authorization, "Bearer $SUPABASE_ANON_KEY")
            }
            setBody(bodyMap)
        }.bodyAsText()
        println("⬅️ Response from $url: $responseText")
        return responseText
    }

    suspend fun login(email: String, password: String): ApiResponse {
        val responseText = postRequest(
            "$SUPABASE_URL/functions/v1/login",
            mapOf("email" to email, "password" to password)
        )

        val body = Json.parseToJsonElement(responseText).jsonObject

        val message = body["message"]?.jsonPrimitive?.content
        val error = body["error"]?.jsonPrimitive?.content
        accessToken = body["access_token"]?.jsonPrimitive?.content
        val user = body["user"]?.let {
            Json.decodeFromJsonElement<UserDto>(it)
        }

        return ApiResponse(
            message = message,
            error = error,
            access_token = accessToken,
            refresh_token = body["refresh_token"]?.jsonPrimitive?.content,
            user = user
        )
    }

    suspend fun signup(
        email: String,
        password: String,
        fullName: String,
        phone: String
    ): ApiResponse {
        val responseText = postRequest(
            "$SUPABASE_URL/functions/v1/signup",
            mapOf(
                "action" to "signup",
                "email" to email,
                "password" to password,
                "full_name" to fullName,
                "phone" to phone
            )
        )

        val body = Json.parseToJsonElement(responseText).jsonObject
        val message = body["message"]?.jsonPrimitive?.content
        val error = body["error"]?.jsonPrimitive?.content

        return ApiResponse(
            message = message,
            error = error
        )
    }

    suspend fun resendOtp(email: String): ApiResponse {
        val responseText = postRequest(
            "$SUPABASE_URL/functions/v1/signup",
            mapOf("action" to "resend_otp", "email" to email)
        )

        val body = Json.parseToJsonElement(responseText).jsonObject
        val message = body["message"]?.jsonPrimitive?.content
        val error = body["error"]?.jsonPrimitive?.content

        return ApiResponse(
            message = message,
            error = error
        )
    }

    suspend fun verifyOtp(email: String, otp: String): ApiResponse {
        val responseText = postRequest(
            "$SUPABASE_URL/functions/v1/otp-verify-signup",
            mapOf("email" to email, "otp" to otp)
        )

        val body = Json.parseToJsonElement(responseText).jsonObject
        val message = body["message"]?.jsonPrimitive?.content
        val error = body["error"]?.jsonPrimitive?.content
        accessToken = body["access_token"]?.jsonPrimitive?.content
        val user = body["user"]?.let {
            Json.decodeFromJsonElement<UserDto>(it)
        }

        return ApiResponse(
            message = message,
            error = error,
            access_token = accessToken,
            refresh_token = body["refresh_token"]?.jsonPrimitive?.content,
            user = user
        )
    }

    suspend fun updateProfile(
        email: String,
        fullName: String,
        phone: String,
        userId: String
    ): ApiResponse {
        val responseText = postRequest(
            "$SUPABASE_URL/functions/v1/profile-update",
            mapOf("userId" to userId, "email" to email, "full_name" to fullName, "phone" to phone)
        )

        val body = Json.parseToJsonElement(responseText).jsonObject

        val message = body["message"]?.jsonPrimitive?.content
        val error = body["error"]?.jsonPrimitive?.content
        val user = body["userData"]?.let {
            Json.decodeFromJsonElement<UserDto>(it)
        }

        return ApiResponse(
            message = message,
            error = error,
            user = user
        )
    }

    suspend fun logout(): ApiResponse {
        accessToken?.let {
            val responseText = postRequest(
                "$SUPABASE_URL/auth/v1/logout",
                bodyMap = TODO(),
            )
            println("Logout response: $responseText")

            val body = Json.parseToJsonElement(responseText).jsonObject
            val message = body["message"]?.jsonPrimitive?.content
            val error = body["error"]?.jsonPrimitive?.content
            accessToken = null
            return ApiResponse(message = message, error = error)
        }
        return ApiResponse(error = "No active session")
    }

    suspend fun addExpense(
        title: String,
        description: String?,
        amount: Double,
        date: String,
        userId: String
    ): ApiResponse {

        val request = AddExpenseRequest(
            title = title,
            description = description,
            amount = amount,
            date = date,
            user_id = userId
        )

        val responseText = postRequest(
            "$SUPABASE_URL/functions/v1/add-expense",
            request
        )

        val body = Json.parseToJsonElement(responseText).jsonObject
        Log.d("ADD_EXPENSE", "Response = $body")

        val message = body["message"]?.jsonPrimitive?.content
        val error = body["error"]?.jsonPrimitive?.content
        val expense = body["data"]?.let {
            Json.decodeFromJsonElement<ExpenseDto>(it)
        }


        return ApiResponse(
            message = message,
            error = error
        )
    }

    suspend fun updateExpense(
        expenseId: Long,
        title: String,
        description: String?,
        amount: Double,
        date: String,
        userId: String
    ): ApiResponse {
        val request = UpdateExpenseRequest(
            id = expenseId,
            title = title,
            description = description,
            amount = amount,
            date = date,
            user_id = userId
        )
        val responseText = postRequest(
            "$SUPABASE_URL/functions/v1/update-expense",
            request
        )

        val body = Json.parseToJsonElement(responseText).jsonObject
        Log.d("UPDATE_EXPENSE", "Response = $body")

        val message = body["message"]?.jsonPrimitive?.content
        val error = body["error"]?.jsonPrimitive?.content
        val expense = body["data"]?.let {
            Json.decodeFromJsonElement<ExpenseDto>(it)
        }

        return ApiResponse(
            message = message,
            error = error
        )
    }

    suspend fun deleteExpense(
        expenseId: Long,
        userId: String
    ): ApiResponse {

        val responseText = postRequest(
            "$SUPABASE_URL/functions/v1/delete-expense",
            mapOf(
                "id" to expenseId,
                "user_id" to userId
            )
        )

        val body = Json.parseToJsonElement(responseText).jsonObject

        val message = body["message"]?.jsonPrimitive?.content
        val error = body["error"]?.jsonPrimitive?.content
        val data = body["data"]?.jsonPrimitive?.content

        return ApiResponse(
            message = message,
            error = error
        )
    }

    //    suspend fun getExpenses(userId: String,month: Int?): ExpenseListResponse {
//
////        val request = mapOf("user_id" to userId)
//        val request = GetExpenseRequest(
//            user_id = userId,
//            month = month
//        )
//
//
//        val responseText = postRequest(
//            "$SUPABASE_URL/functions/v1/get-expense",
//            request
//        )
//
//        val body = Json.parseToJsonElement(responseText).jsonObject
//        Log.d("GET_EXPENSE", "Response = $body")
//
//        val expenses = body["data"]
//            ?.let { json ->
//                Json.decodeFromJsonElement<List<ExpenseDto>>(json)
//                    .map { dto ->
//                        Expense(
//                            id = dto.id,
//                            title = dto.title,
//                            description = dto.description,
//                            amount = dto.amount,
//                            date = dto.date
//                        )
//                    }
//            } ?: emptyList()
//
//        return ExpenseListResponse(
//            expenses = expenses,
//            message = body["message"]?.jsonPrimitive?.content,
//            error = body["error"]?.jsonPrimitive?.content
//        )
//    }
    suspend fun getExpenses(
        month: Int?,
        year: Int?
    ): ExpenseListResponse {

        val body = mutableMapOf<String, Any>()
        if (month != null) body["month"] = month
        if (year != null) body["year"] = year

        val responseText = postRequest(
            "$SUPABASE_URL/functions/v1/get-expense",
            body
        )

        val json = Json.parseToJsonElement(responseText).jsonObject
        Log.d("GET_EXPENSE", "Response = $json")

        val expenses = json["data"]?.let {
            Json.decodeFromJsonElement<List<ExpenseDto>>(it).map { dto ->
                Expense(
                    id = dto.id,
                    title = dto.title,
                    description = dto.description,
                    amount = dto.amount,
                    date = dto.date
                )
            }
        } ?: emptyList()

        return ExpenseListResponse(
            expenses = expenses,
            message = json["message"]?.jsonPrimitive?.content,
            error = json["error"]?.jsonPrimitive?.content
        )
    }


}

@Serializable
data class GetExpenseRequest(
    val user_id: String,
    val month: Int?

)

@Serializable
data class AddExpenseRequest(
    val title: String,
    val description: String? = null,
    val amount: Double,
    val date: String,
    val user_id: String
)

@Serializable
data class UpdateExpenseRequest(
    val id: Long,
    val title: String,
    val description: String? = null,
    val amount: Double,
    val date: String,
    val user_id: String
)

@Serializable
data class ExpenseDto(
    val id: Long,
    val title: String,
    val description: String? = null,
    val amount: Double,
    val date: String,
    val created_by: String,
    val updated_by: String,
    val created_date: String,
    val updated_date: String
)
