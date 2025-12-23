package com.example.expansetracker.ui.expense

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YearFilter(
    selectedYear: Int,
    onYearChange: (Int) -> Unit
) {
    val currentYear = remember {
        Calendar.getInstance().get(Calendar.YEAR)
    }

    // 👇 Include next year (e.g. 2026)
    val years = remember {
        (currentYear + 1 downTo currentYear - 5).toList()
    }

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedYear.toString(),
            onValueChange = {},
            readOnly = true,
            label = { Text("Year") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            years.forEach { year ->
                DropdownMenuItem(
                    text = { Text(year.toString()) },
                    onClick = {
                        expanded = false
                        onYearChange(year) // 🔥 triggers API via LaunchedEffect
                    }
                )
            }
        }
    }
}
