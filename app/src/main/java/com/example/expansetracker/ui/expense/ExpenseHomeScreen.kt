package com.example.expansetracker.ui.expense

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.expansetracker.ui.components.AppDrawer
import com.example.expansetracker.ui.components.ExpenseItemCard
import com.example.expansetracker.ui.components.MonthFilter
import com.example.expansetracker.ui.components.TotalExpenseCard
import com.example.expansetracker.ui.components.YearFilter
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

    val context = LocalContext.current

    // -------------------- Filter State --------------------
    var selectedMonthInt by remember { mutableStateOf(-1) } // -1 = All
    var selectedMonthLabel by remember { mutableStateOf("All") }

    val currentYear = remember { Calendar.getInstance().get(Calendar.YEAR) }
    var selectedYear by remember { mutableStateOf(currentYear) }

    val monthNameMap = mapOf(
        1 to "Jan", 2 to "Feb", 3 to "Mar", 4 to "Apr",
        5 to "May", 6 to "Jun", 7 to "Jul", 8 to "Aug",
        9 to "Sep", 10 to "Oct", 11 to "Nov", 12 to "Dec"
    )

    // -------------------- Drawer --------------------
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // -------------------- Filtered Expenses --------------------
    val filteredExpenses by viewModel.getExpensesFiltered(
        month = if (selectedMonthInt == -1) null else selectedMonthInt,
        year = selectedYear
    ).collectAsState(initial = emptyList())

    val totalExpense = filteredExpenses.sumOf { it.amount }

    // -------------------- UI --------------------
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.75f)
                    .clip(RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp))
            ) {
                AppDrawer(navController = navController)
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Dashboard", color = white) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, null, tint = white)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Purple40
                    ),
                    actions = {
                        YearFilter(
                            selectedYear = selectedYear,
                            onYearChange = { year ->
                                selectedYear = year
                            }
                        )
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate("add_edit") },
                    containerColor = Purple40
                ) {
                    Icon(Icons.Default.Add, null)
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // -------- TOTAL EXPENSE --------
                item {
                    TotalExpenseCard(total = totalExpense)
                }

                // -------- FILTERS --------
                item {
                    MonthFilter(
                        selectedMonth = selectedMonthLabel,
                        onMonthChange = { monthInt ->
                            selectedMonthInt = monthInt
                            selectedMonthLabel =
                                if (monthInt == -1) "All" else monthNameMap[monthInt] ?: "All"
                        }
                    )
                }

                // -------- EXPENSE LIST --------
                if (filteredExpenses.isEmpty()) {
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
                    items(filteredExpenses, key = { it.id }) { expense ->
                        ExpenseItemCard(
                            expense = expense,
                            onEdit = {
                                navController.navigate("add_edit?expenseId=${expense.id}")
                            },
                            onDelete = {
                                viewModel.deleteExpense(expense.id)
                            }
                        )
                    }
                }
            }
        }
    }
}
