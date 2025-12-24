package com.example.expansetracker.data.model

data class ExpenseModel(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val amount: Double = 0.0,
    val date: Long = 0L,
    val createdAt: Long = System.currentTimeMillis()
)
