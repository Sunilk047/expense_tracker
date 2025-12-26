package com.example.expansetracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expansetracker.data.model.ExpenseModel
import com.example.expansetracker.data.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancelChildren
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {

    val expenses: StateFlow<List<ExpenseModel>> =
        repository.getExpenses()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addExpense(title: String, amount: Double, desc: String, date: Long) {
        viewModelScope.launch {
            repository.addExpense(
                ExpenseModel(
                    title = title,
                    description = desc,
                    amount = amount,
                    date = date
                )
            )
        }
    }

    fun getExpensesFiltered(month: Int?, year: Int?): Flow<List<ExpenseModel>> {
        return expenses.map { list ->
            list.filter { expense ->
                val cal = Calendar.getInstance().apply { timeInMillis = expense.date }
                val expenseMonth = cal.get(Calendar.MONTH) + 1 // Calendar.MONTH is 0-based
                val expenseYear = cal.get(Calendar.YEAR)
                val monthMatches = month == null || month == expenseMonth
                val yearMatches = year == null || year == expenseYear
                monthMatches && yearMatches
            }
        }
    }


    fun updateExpense(expense: ExpenseModel) {
        viewModelScope.launch {
            repository.updateExpense(expense)
        }
    }

    fun deleteExpense(expenseId: String) {
        viewModelScope.launch {
            repository.deleteExpense(expenseId)
        }
    }
    fun clearOnLogout() {
        // Cancels flow collection
        viewModelScope.coroutineContext.cancelChildren()
    }
}