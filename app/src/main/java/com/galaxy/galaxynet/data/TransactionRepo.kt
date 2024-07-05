package com.galaxy.galaxynet.data

import com.galaxy.galaxynet.model.TransactionHistory
import com.galaxy.util.TransactionResult

interface TransactionRepo {
    suspend fun addTransaction(transaction: TransactionHistory): TransactionResult
    suspend fun getAllTransactions(): List<TransactionHistory>
}