package com.example.expansetracker.ui.expense

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MonthFilter(
    selectedMonth: String,
    onMonthChange: (Int) -> Unit   // ← always Int
) {
    val months = listOf(
        "All", "Jan", "Feb", "Mar", "Apr", "May",
        "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )

    val monthMap = mapOf(
        "All" to -1,
        "Jan" to 1, "Feb" to 2, "Mar" to 3, "Apr" to 4,
        "May" to 5, "Jun" to 6, "Jul" to 7, "Aug" to 8,
        "Sep" to 9, "Oct" to 10, "Nov" to 11, "Dec" to 12
    )

    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        months.forEach { month ->
            FilterChip(
                selected = selectedMonth == month,
                onClick = {
                    onMonthChange(monthMap[month] ?: -1)
                },
                label = { Text(month) }
            )
        }
    }
}

