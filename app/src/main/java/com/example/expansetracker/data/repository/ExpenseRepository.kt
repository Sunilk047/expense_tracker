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

    private fun expensesRef(uid: String) =
        firestore.collection("expenses")
            .document(uid)
            .collection("items")

    /* ---------------- REALTIME TODOS ---------------- */

    fun getExpenses(): Flow<List<ExpenseModel>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = expensesRef(uid)
            .orderBy("createdAt")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // ✅ IGNORE PERMISSION DENIED (logout case)
                    if (error.code == com.google.firebase.firestore.FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                        trySend(emptyList())
                        return@addSnapshotListener
                    }
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
        auth.currentUser?.uid?.let { uid ->
            expensesRef(uid).add(expense)
        }
    }

    /* ---------------- UPDATE ---------------- */

    suspend fun updateExpense(expense: ExpenseModel) {
        auth.currentUser?.uid?.let { uid ->
            expensesRef(uid).document(expense.id).set(expense)
        }
    }

    /* ---------------- DELETE ---------------- */

    suspend fun deleteExpense(expenseId: String) {
        auth.currentUser?.uid?.let { uid ->
            expensesRef(uid).document(expenseId).delete()
        }
    }
}
