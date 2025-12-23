package com.example.expansetracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense")
data class Expense(
    @PrimaryKey
    val id: Long,
    val title: String,
    val description: String?,
    val amount: Double,
    val date: String
)
