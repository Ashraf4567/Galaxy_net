package com.galaxy.galaxynet.data.usersRepo


import android.util.Log
import com.galaxy.galaxynet.model.Token
import com.galaxy.galaxynet.model.User
import com.galaxy.util.UserResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
            Log.d("getAllUsers", "QuerySnapshot: ${querySnapshot.toObjects(User::class.java)}")
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

    override suspend fun updateUserPoints(userId: String, delta: Int): Result<Unit> {
        return try {
            // Get the user document from Firestore
            val userDocument = usersCollection.document(userId).get().await()

            // Convert the document snapshot to a User object
            val user = userDocument.toObject(User::class.java)

            // Check if the user document exists
            if (user != null) {
                // Calculate the updated points by adding the delta
                val updatedPoints = user.points?.plus(delta)

                // Ensure the points are not negative
                if (updatedPoints?:0 >= 0) {
                    // Create a map with the updated points
                    val updatedData = mapOf(
                        "points" to updatedPoints
                    )

                    // Update the user document with the new points
                    usersCollection.document(userId).update(updatedData).await()

                    Result.success(Unit)
                } else {
                    // Return failure if the points would become negative
                    throw Exception("Points cannot be negative")
                }
            } else {
                // User document not found
                Result.failure(Exception("User not found"))
            }
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }


    override suspend fun disableUser(userId: String): Result<Unit> {
        return try {
            val user = usersCollection.document(userId)
            user.update("active" , false).await()
             tokensCollection.document(userId).delete().await()

            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    override suspend fun activeUser(userId: String): Result<Unit> {
        return try {
            val user = usersCollection.document(userId)
            user.update("active", true).await()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

}