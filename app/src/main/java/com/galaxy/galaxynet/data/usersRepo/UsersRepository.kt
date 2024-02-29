package com.galaxy.galaxynet.data.usersRepo

import com.galaxy.galaxynet.model.User
import com.galaxy.util.UserResult


interface UsersRepository {
    suspend fun insertUserToFirestore(user: User): Result<Unit>
    suspend fun getUserById(userId: String?): UserResult
    suspend fun getAllUsers(): List<User>

}