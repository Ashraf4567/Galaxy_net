package com.galaxy.galaxynet.data.usersRepo


import android.util.Log
import com.galaxy.galaxynet.model.User
import com.galaxy.util.UserResult
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UsersRepositoryImpl @Inject constructor(
    firebaseFirestore: FirebaseFirestore
) : UsersRepository {
    private val usersCollection = firebaseFirestore.collection(User.COLLECTION_NAME)

    override suspend fun insertUserToFirestore(user: User): Result<Unit> {
        return try {
            val userId = user.id ?: usersCollection.document().id
            usersCollection.document(userId).set(user)
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

}