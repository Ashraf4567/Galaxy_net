package com.galaxy.galaxynet.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class TransactionHistory(
    val points: Int,
    val employeeName: String,
    val employeeId: String,
    val transactionDate: String = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a")),
    val transactionNotes: String,
    val transactionType: String
){
    companion object{
        const val COLLECTION_NAME = "transaction"
    }
}

enum class TransactionType(val value: String){
    ADD("اضافه"),
    REMOVE("خصم")
}
