package com.example.expansetracker.data.repository

import com.example.expansetracker.data.local.UserSessionManager
import com.example.expansetracker.data.remote.SupabaseApi
import com.example.expansetracker.data.remote.SupabaseApi.ApiResponse
import javax.inject.Inject

class ExpenseRepository @Inject constructor(
    private val sessionManager: UserSessionManager
) {

    /**
     * Add new expense
     */
    suspend fun addExpense(
        title: String,
        description: String?,
        amount: Double,
        date: String
    ): ApiResponse {

        val userId = sessionManager.getUser()?.id
            ?: return ApiResponse(error = "User not logged in")

        return SupabaseApi.addExpense(
            title = title,
            description = description,
            amount = amount,
            date = date,
            userId = userId
        )
    }

    /**
     * Update existing expense
     */
    suspend fun updateExpense(
        expenseId: Long,
        title: String,
        description: String?,
        amount: Double,
        date: String
    ): ApiResponse {

        val userId = sessionManager.getUser()?.id
            ?: return ApiResponse(error = "User not logged in")

        return SupabaseApi.updateExpense(
            expenseId = expenseId,
            title = title,
            description = description,
            amount = amount,
            date = date,
            userId = userId
        )
    }

    /**
     * Get all expenses for logged-in user
     * (to be used with expense_list Edge Function)
     */
    suspend fun getExpenses(month: Int?, year: Int?): SupabaseApi.ExpenseListResponse {
        return SupabaseApi.getExpenses(month, year)
    }



    /**
     * Delete expense
     * (to be used with expense_delete Edge Function)
     */
    suspend fun deleteExpense(expenseId: Long): ApiResponse {

        val userId = sessionManager.getUser()?.id
            ?: return ApiResponse(error = "User not logged in")

        return SupabaseApi.deleteExpense(
            expenseId = expenseId,
            userId = userId
        )
    }
}
