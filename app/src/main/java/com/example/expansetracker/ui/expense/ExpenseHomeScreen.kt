package com.example.expansetracker.ui.expense

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.expansetracker.ui.components.AppDrawer
import com.example.expansetracker.ui.theme.Purple40
import com.example.expansetracker.ui.theme.white
import com.example.expansetracker.viewmodel.ExpenseViewModel
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseHomeScreen(
    navController: NavController,
    viewModel: ExpenseViewModel = hiltViewModel()
) {

    /* -------------------- STATE -------------------- */

    val context = LocalContext.current
    val expenses by viewModel.expenses.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var selectedMonthInt by remember { mutableStateOf(-1) } // -1 = All
    var selectedMonthLabel by remember { mutableStateOf("All") }

    val currentYear = remember {
        Calendar.getInstance().get(Calendar.YEAR)
    }
    var selectedYear by remember { mutableStateOf(currentYear) }

    val monthNameMap = mapOf(
        1 to "Jan", 2 to "Feb", 3 to "Mar", 4 to "Apr",
        5 to "May", 6 to "Jun", 7 to "Jul", 8 to "Aug",
        9 to "Sep", 10 to "Oct", 11 to "Nov", 12 to "Dec"
    )

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    /* -------------------- AUTO API RELOAD -------------------- */

    LaunchedEffect(selectedMonthInt, selectedYear) {
        viewModel.loadExpenses(
            month = if (selectedMonthInt == -1) null else selectedMonthInt,
            year = selectedYear
        )
    }

    val totalExpense = expenses.sumOf { it.amount }

    /* -------------------- UI -------------------- */

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(navController = navController)
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Dashboard", color = white) },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } }
                        ) {
                            Icon(Icons.Default.Menu, null, tint = white)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Purple40
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate("add") },
                    containerColor = Purple40
                ) {
                    Icon(Icons.Default.Add, null)
                }
            }
        ) { padding ->

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    /* -------- TOTAL -------- */
                    item {
                        TotalExpenseCard(total = totalExpense)
                    }

                    /* -------- FILTERS -------- */
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                            MonthFilter(
                                selectedMonth = selectedMonthLabel,
                                onMonthChange = { monthInt ->
                                    selectedMonthInt = monthInt
                                    selectedMonthLabel =
                                        if (monthInt == -1) "All"
                                        else monthNameMap[monthInt] ?: "All"
                                }
                            )

                            YearFilter(
                                selectedYear = selectedYear,
                                onYearChange = { year ->
                                    selectedYear = year
                                }
                            )
                        }
                    }

                    /* -------- LIST -------- */
                    if (expenses.isEmpty()) {
                        item {
                            Text(
                                text = "No expenses found",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
//                        items(expenses, key = { it.id }) { expense ->
//                            SwipeExpenseCard(
//                                expense = expense,
//                                onDelete = {
//                                    // optional delete logic
//                                }
//                            )
//                        }
                        items(expenses, key = { it.id }) { expense ->
                            ExpenseItemCard(
                                expense = expense,
                                onEdit = {
                                    navController.navigate("add?expenseId=${expense.id}")
                                },
                                onDelete = {
                                    viewModel.deleteExpense(expense.id) {
                                        message->
                                        Toast
                                            .makeText(context, message, Toast.LENGTH_SHORT)
                                            .show()
                                        // Optional toast/snackbar
                                    }
                                }
                            )
                        }

                    }
                }
//                Spacer(Modifier.height(20.dp))

            }
        }
    }
}
