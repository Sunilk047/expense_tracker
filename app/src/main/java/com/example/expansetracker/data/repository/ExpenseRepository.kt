package com.example.expansetracker.data.repository

import com.example.expansetracker.data.model.ExpenseModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class ExpenseRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private fun expensesRef() =
        firestore.collection("expenses")
            .document(auth.currentUser!!.uid)
            .collection("items")

    /* ---------------- REALTIME TODOS ---------------- */

    fun getExpenses(): Flow<List<ExpenseModel>> = callbackFlow {
        val listener = expensesRef()
            .orderBy("createdAt")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val expenses = snapshot?.documents?.mapNotNull {
                    it.toObject(ExpenseModel::class.java)?.copy(id = it.id)
                } ?: emptyList()

                trySend(expenses)
            }

        awaitClose { listener.remove() }
    }

    /* ---------------- ADD ---------------- */

    suspend fun addExpense(expense: ExpenseModel) {
        expensesRef().add(expense)
    }

    /* ---------------- UPDATE ---------------- */

    suspend fun updateExpense(expense: ExpenseModel) {
        expensesRef().document(expense.id).set(expense)
    }

    /* ---------------- DELETE ---------------- */

    suspend fun deleteExpense(expenseId: String) {
        expensesRef().document(expenseId).delete()
    }
}

