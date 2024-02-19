package com.galaxy.util

import com.google.firebase.firestore.DocumentReference

open class AddTaskResult {
    data class Success(val documentReference: DocumentReference) : AddTaskResult()
    data class Failure(val exception: Exception) : AddTaskResult()
}