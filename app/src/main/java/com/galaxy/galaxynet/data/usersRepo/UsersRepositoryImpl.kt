package com.galaxy.galaxynet.data.usersRepo


import android.util.Log
import com.galaxy.galaxynet.model.Token
import com.galaxy.galaxynet.model.User
import com.galaxy.util.UserResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UsersRepositoryImpl @Inject constructor(
    firebaseFirestore: FirebaseFirestore
) : UsersRepository {
    private val usersCollection = firebaseFirestore.collection(User.COLLECTION_NAME)
    private val tokensCollection = firebaseFirestore.collection(Token.TOKENS_COLLECTION)

    override suspend fun insertUserToFirestore(user: User): Result<Unit> {
        return try {
            val userId = user.id ?: usersCollection.document().id
            usersCollection.document(userId).set(user)
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    override fun saveUserToken(token: Token): Result<Unit> {
        return try {
            tokensCollection.document(token.id).set(token)
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    override suspend fun getUserById(userId: String?): UserResult {
        return try {
            val userSnapshot = usersCollection.document(userId ?: "").get().await()
            UserResult.Success(userSnapshot.toObject(User::class.java))
        } catch (exception: Exception) {
            Log.e("getUserById", "Exception: ${exception.localizedMessage}")
            UserResult.Failure(exception)
        }
    }

    override suspend fun getAllUsers(): List<User> {
        try {
            val querySnapshot = usersCollection.get().await()
            return querySnapshot.toObjects(User::class.java)
        } catch (e: FirebaseFirestoreException) {
            Log.e("getAllUsers", "FirebaseFirestoreException: ${e.message}", e)
            throw e
        } catch (e: Exception) {
            Log.e("getAllUsers", "Unexpected exception: ${e.message}", e)
            throw Exception("An error occurred while retrieving users.")
        }
    }

    override suspend fun getAllTokens(): List<Token> {
        try {
            val querySnapshot = tokensCollection.get().await()
            return querySnapshot.toObjects(Token::class.java)
        } catch (e: FirebaseFirestoreException) {
            throw e
        } catch (e: Exception) {
            throw Exception("An error occurred while retrieving users.")
        }
    }

    override suspend fun deleteUser(userId: String): Result<Unit> {
        return try {
            usersCollection.document(userId).delete().await()
            // Consider deleting associated tokens as well
             tokensCollection.document(userId).delete().await()

            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

}