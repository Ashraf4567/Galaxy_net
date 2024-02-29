package com.galaxy.util

sealed class TransactionResult {
    class Success() : TransactionResult()
    data class Failure(val exception: Exception?) : TransactionResult()
}
