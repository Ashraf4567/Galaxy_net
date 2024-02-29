package com.galaxy.galaxynet.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Parcelize
data class Task(
    val id: String? = null,
    val title: String? = null,
    val description: String? = null,
    val taskCompletionState: String? = null,
    val dateTime: String = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a")),
    val creatorName: String? = null,
    val taskCategory: String? = null,
    val taskAcceptanceStatus: String? = null,
    val points: String? = null,
    val workerName: String? = null
) : Parcelable {
    companion object {
        const val COLLECTION_NAME = "tasks"
    }

    fun getFormattedTime(): String {
        val localDateTime =
            LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a"))
        return localDateTime.format(DateTimeFormatter.ofPattern("h:mm a"))
    }


}

