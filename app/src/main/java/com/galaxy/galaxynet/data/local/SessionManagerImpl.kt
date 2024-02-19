package com.galaxy.galaxynet.data.local

import android.content.SharedPreferences
import com.galaxy.galaxynet.model.User
import com.google.gson.Gson
import javax.inject.Inject

class SessionManagerImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : SessionManager {
    companion object {
        private const val KEY_USER_DATA = "user_data"
    }

    private val gson = Gson()

    // Function to save user data
    override fun saveUserData(user: User?) {
        val userDataJson = Gson().toJson(user)
        sharedPreferences.edit().putString(KEY_USER_DATA, userDataJson).apply()
    }

    // Function to retrieve user data
    override fun getUserData(): User? {
        val userDataJson = sharedPreferences.getString(KEY_USER_DATA, null)
        return if (userDataJson != null) {
            Gson().fromJson(userDataJson, User::class.java)
        } else {
            null
        }
    }


    // Function to clear user data and authentication token (logout)
    override fun logout() {
        sharedPreferences.edit().remove(KEY_USER_DATA).remove(KEY_USER_DATA).apply()
    }
}