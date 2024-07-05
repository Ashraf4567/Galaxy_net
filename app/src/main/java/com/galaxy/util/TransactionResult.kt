package com.galaxy.util

sealed class TransactionResult {
    object Success : TransactionResult()
    data class Failure(val exception: Exception?) : TransactionResult()
}
