package com.galaxy.galaxynet.data

import android.util.Log
import com.galaxy.galaxynet.model.TransactionHistory
import com.galaxy.galaxynet.model.TransactionType
import com.galaxy.util.TransactionResult
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.util.Date
import javax.inject.Inject

class TransactionsRepoImpl @Inject constructor(
    firestore: FirebaseFirestore
): TransactionRepo {

    private val collection = firestore.collection(TransactionHistory.COLLECTION_NAME)

    override suspend fun addTransaction(transaction: TransactionHistory): TransactionResult {
        return try {
            collection.add(transaction).await()
            TransactionResult.Success
        }catch (e: Exception) {
            TransactionResult.Failure(e)
        }
    }

    override suspend fun getAllTransactions(): List<TransactionHistory> {
        return try {
            val list = mutableListOf<TransactionHistory>()
            val snapshot = collection.get().await()
            snapshot .documents .forEach {

                val points=it.get("points") as Long
                val x=
                    TransactionHistory(
                        points = points.toInt(),
                        employeeName = it.get("employeeName") as String,
                        transactionType = it.get("transactionType") as String,
                        transactionDate = it.get("transactionDate") as String,
                        transactionNotes = it.get("transactionNotes") as String,
                        employeeId = it.get("employeeId") as String
                    )
                list.add(x)
            }

            list.toList()

        }catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getTransactionsById(id: String): List<TransactionHistory> {
        try {
            val list = mutableListOf<TransactionHistory>()
            val snapshot = collection.whereEqualTo("employeeId", id).get().await()
            snapshot .documents .forEach {
                val points=it.get("points") as Long
                val x=
                    TransactionHistory(
                        points = points.toInt(),
                        employeeName = it.get("employeeName") as String,
                        transactionType = it.get("transactionType") as String,
                        transactionDate = it.get("transactionDate") as String,
                        transactionNotes = it.get("transactionNotes") as String,
                        employeeId = it.get("employeeId") as String
                    )
                list.add(x)

            }
            return list.toList()
        } catch (e: Exception) {
            throw e
        }
    }
}