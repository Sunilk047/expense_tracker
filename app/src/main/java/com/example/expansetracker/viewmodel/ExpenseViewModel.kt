package com.example.expansetracker.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expansetracker.data.local.Expense
import com.example.expansetracker.data.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadExpenses(month: Int?, year: Int?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getExpenses(month, year)
                _expenses.value = response.expenses
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addExpense(
        title: String,
        description: String?,
        amount: Double,
        date: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val res = repository.addExpense(title, description, amount, date)
            _isLoading.value = false
            Log.d("AddEXPANSE", "EXPANSE = ${res}")

            if (res.error != null) {
                onResult(false, res.error)
            } else {
                onResult(true, res.message ?: "Expense added")
            }
        }
    }

    fun updateExpense(
        id: Long,
        title: String,
        description: String?,
        amount: Double,
        date: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val res = repository.updateExpense(id, title, description, amount, date)
            _isLoading.value = false

            if (res.error != null) {
                onResult(false, res.error)
            } else {
                onResult(true, res.message ?: "Expense updated")
            }
        }
    }


    //    fun deleteExpense(expenseId: Long, onResult: (String) -> Unit) {
//        viewModelScope.launch {
//            val res = repository.deleteExpense(expenseId)
//            onResult(res.message ?: res.error ?: "Done")
////            loadExpenses()
//        }
//    }
    fun deleteExpense(
        expenseId: Long,
        onResult: (String) -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.deleteExpense(expenseId)

            if (res.error == null) {
                // 🔥 REMOVE ITEM FROM LIST
                _expenses.value = _expenses.value.filterNot {
                    it.id == expenseId
                }
            }

            onResult(res.message ?: res.error ?: "Deleted")
        }
    }

}
