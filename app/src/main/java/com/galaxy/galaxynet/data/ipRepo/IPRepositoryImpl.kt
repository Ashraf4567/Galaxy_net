package com.galaxy.galaxynet.data.ipRepo

import com.galaxy.galaxynet.model.Ip
import com.galaxy.util.TransactionResult
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class IPRepositoryImpl @Inject constructor(
    val firestore: FirebaseFirestore
) : IpRepository {

    private val deviceTypeCollection = firestore.collection(Ip.DEVICE_TYPE_COLLECTION_NAME)
    override suspend fun addDeviceType(type: String): TransactionResult {
        return try {
            val result = deviceTypeCollection.add(type).await()
            TransactionResult.Success()
        } catch (e: Exception) {
            TransactionResult.Failure(e)
        }
    }
}