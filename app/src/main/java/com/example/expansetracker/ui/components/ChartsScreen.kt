package com.example.expansetracker.ui.expense

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ChartsScreen() {

    val data = listOf(
        "Jan" to 2000f,
        "Feb" to 1500f,
        "Mar" to 2500f,
        "Apr" to 1800f
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Monthly Expense Chart",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(24.dp))

        data.forEach { (month, value) ->
            Text(month)
            LinearProgressIndicator(
                progress = value / 3000f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}
