package com.galaxy.util

import com.galaxy.galaxynet.model.User

sealed class UserResult {
    data class Success(val user: User?) : UserResult()
    data class Failure(val exception: Exception) : UserResult()
}