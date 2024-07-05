package com.galaxy.galaxynet.data.usersRepo

import com.galaxy.galaxynet.model.Token
import com.galaxy.galaxynet.model.User
import com.galaxy.util.UserResult


interface UsersRepository {
    suspend fun insertUserToFirestore(user: User): Result<Unit>
    suspend fun getUserById(userId: String?): UserResult
    suspend fun getAllUsers(): List<User>
    fun saveUserToken(token: Token): Result<Unit>
    suspend fun getAllTokens(): List<Token>
    suspend fun disableUser(userId: String): Result<Unit>
    suspend fun activeUser(userId: String): Result<Unit>
    suspend fun updateUserPoints(userId: String, delta: Int): Result<Unit>
}