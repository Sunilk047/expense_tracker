package com.example.expansetracker.ui.expense

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.expansetracker.ui.components.AppButton
import com.example.expansetracker.ui.components.AppTextField
import com.example.expansetracker.ui.theme.Purple40
import com.example.expansetracker.ui.theme.white
import com.example.expansetracker.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditExpenseScreen(
    navController: NavController,
    expenseId: String? = null,
    viewModel: ExpenseViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val expenses by viewModel.expenses.collectAsState()

    val isEditMode = expenseId != null
    val existingExpense = expenses.firstOrNull { it.id == expenseId }

    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var error by remember { mutableStateOf<String?>(null) }
    var titleError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }

    val calendar = remember { Calendar.getInstance() }
    val uiDateFormat = remember {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    }
    /* 🔑 PREFILL WHEN EDIT MODE */
    LaunchedEffect(existingExpense) {
        existingExpense?.let {
            title = it.title
            description = it.description
            amount = it.amount.toString()
            selectedDate = it.date
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditMode) "Edit Expense" else "Add Expense",
                        color = white
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, null, tint = white)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Purple40
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Surface(
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 8.dp,
                color = white,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    /* -------- Title -------- */
                    AppTextField(
                        value = title,
                        label = "Expense Title",
                        leadingIcon = { Icon(Icons.Default.List, null) },
                        isError = titleError,
                        onValueChange = {
                            title = it
                            titleError = false
                        }
                    )

                    Spacer(Modifier.height(8.dp))

//                    OutlinedTextField(
//                        value = amount,
//                        onValueChange = { amount = it },
//                        label = { Text("Amount") },
//                        leadingIcon = { Icon(Icons.Filled.Money, null) },
//                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                        modifier = Modifier.fillMaxWidth()
//                    )
                    /* -------- Amount -------- */
                    AppTextField(
                        value = amount,
                        label = "Amount",
                        leadingIcon = { Icon(Icons.Filled.Money, null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        onValueChange = { amount = it }
                    )

                    Spacer(Modifier.height(8.dp))

                    /* -------- Description -------- */
                    AppTextField(
                        value = description,
                        label = "Description",
                        leadingIcon = { Icon(Icons.Default.Description, null) },
                        onValueChange = { description = it }
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = uiDateFormat.format(Date(selectedDate)),
                        onValueChange = {},
                        label = { Text("Date") },
                        readOnly = true,
                        leadingIcon = { Icon(Icons.Filled.DateRange, null) },
                        trailingIcon = {
                            IconButton(onClick = {
                                DatePickerDialog(
                                    context,
                                    { _, y, m, d ->
                                        calendar.set(y, m, d)
                                        selectedDate = calendar.timeInMillis
                                    },
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }) {
                                Icon(Icons.Filled.Edit, null)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(24.dp))

                    /* -------- Save / Update -------- */
                    AppButton(
                        text = if (isEditMode) "Update Expense" else "Save Expense",
                        onClick = {
                            titleError = title.isBlank()
                            amountError = amount.isBlank()
                            if (titleError) {
                                Toast.makeText(
                                    context,
                                    "Title required",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@AppButton
                            }
                            if (amountError) {
                                Toast.makeText(
                                    context,
                                    "Amount required",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@AppButton
                            }

                            if (isEditMode && existingExpense != null) {
                                viewModel.updateExpense(
                                    existingExpense.copy(
                                        title = title,
                                        description = description,
                                        date = selectedDate,
                                        amount = amount.toDouble()
                                    )
                                )
                            } else {
                                viewModel.addExpense(
                                    title = title,
                                    desc = description,
                                    date = selectedDate,
                                    amount = amount.toDouble()
                                )
                            }

                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}
