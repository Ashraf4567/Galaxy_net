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
    private val firestore: FirebaseFirestore
): TransactionRepo {

    private val collection = firestore.collection("transactions")

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

                Log.d("getAllTransactions", ": ${snapshot .documents.size}")
                val points=it.get("points") as Long
                val x=
                    TransactionHistory(
                        points = points.toInt(),
                        employeeName = it.get("employeeName") as String,
                        transactionType = it.get("transactionType") as String,
                        transactionDate = it.get("transactionDate") as String,
                        transactionNotes = it.get("transactionNotes") as String,
                    )
                list.add(x)
            }

            Log.d("getAllTransactions", ": ${list}")
            list.toList()

        }catch (e: Exception) {
            emptyList()
        }
    }
}