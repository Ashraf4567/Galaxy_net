package com.galaxy.galaxynet.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


data class Task(
    val id: String? = null,
    val title: String? = null,
    val description: String? = null,
    val taskCompletionState: String? = null,
    val dateTime: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
    val creatorName: String? = null,
    val taskCategory: String? = null,
    val taskAcceptanceStatus: String? = null,
    val points: String? = null,
    val workerName: String? = null
){
    companion object {
        const val COLLECTION_NAME = "tasks"
    }

}
