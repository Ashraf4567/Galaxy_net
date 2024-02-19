package com.galaxy.galaxynet.data.local

import com.galaxy.galaxynet.model.User

interface SessionManager {
    fun saveUserData(user: User?)
    fun getUserData(): User?
    fun logout()
}