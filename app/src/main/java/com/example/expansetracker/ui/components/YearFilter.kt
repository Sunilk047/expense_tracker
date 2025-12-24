package com.example.expansetracker.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Calendar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YearFilter(
    selectedYear: Int,
    onYearChange: (Int) -> Unit
) {
    val currentYear = remember { Calendar.getInstance().get(Calendar.YEAR) }
    val years = remember { (currentYear + 1 downTo currentYear - 2).toList() }

    var expanded by remember { mutableStateOf(false) }
    var displayedYear by remember { mutableStateOf(selectedYear) } // keeps track of currently displayed year

    // Button with icon and year
    Button(onClick = { expanded = true }) {
        Icon(
            imageVector = Icons.Default.FilterList,
            contentDescription = "Filter"
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(displayedYear.toString()) // show current or selected year
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        years.forEach { year ->
            DropdownMenuItem(
                text = { Text(year.toString()) },
                onClick = {
                    expanded = false
                    displayedYear = year
                    onYearChange(year)
                }
            )
        }
    }
}
