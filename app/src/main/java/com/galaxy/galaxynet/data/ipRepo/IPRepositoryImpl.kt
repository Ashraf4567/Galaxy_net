package com.galaxy.galaxynet.data.ipRepo

import android.util.Log
import com.galaxy.galaxynet.model.DeviceType
import com.galaxy.galaxynet.model.Ip
import com.galaxy.util.TransactionResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class IPRepositoryImpl @Inject constructor(
    val firestore: FirebaseFirestore
) : IpRepository {

    private val deviceTypeCollection = firestore.collection(DeviceType.DEVICE_TYPE_COLLECTION_NAME)
    private val ipsCollection = firestore.collection(Ip.COLLECTION_NAME)
    override suspend fun addDeviceType(type: String): TransactionResult {
        return try {
            val existingTypeQuery = deviceTypeCollection.whereEqualTo("type", type)
            val existingTypeSnapshot = existingTypeQuery.get().await()

            if (existingTypeSnapshot.documents.isNotEmpty()) {
                // Device type already exists
                return TransactionResult.Failure(Exception("Device type already exists"))
            } else {
                // Add the new device type
                deviceTypeCollection.add(DeviceType(type = type)).await()
                return TransactionResult.Success() // Return Success directly
            }
        } catch (e: Exception) {
            Log.e("test add device type", e.message.toString())
            return TransactionResult.Failure(e)
        }
    }

    override suspend fun updateDeviceTypeCounters(): TransactionResult {
        return try {
            // 1. Fetch device types and IPs concurrently
            val deviceTypesTask = deviceTypeCollection.get().await()
            val ipsTask = ipsCollection.get().await()


            for (deviceTypeSnapshot in deviceTypesTask.documents) {
                val deviceType = deviceTypeSnapshot.toObject(DeviceType::class.java)!!

                // 2.1. Find matching IPs efficiently
                val matchingIPs = ipsTask.documents
                    .filter { it.get("deviceType") == deviceType.type }
                    .size


                if (matchingIPs != deviceType.counter) {
                    deviceTypeSnapshot.reference.update("counter", matchingIPs).await()
                }
            }

            TransactionResult.Success()
        } catch (e: Exception) {
            Log.e("Update Device Counters", "Error updating device counters: $e")
            TransactionResult.Failure(e)
        }
    }


    override suspend fun getAllDevicesTypes(): List<String> {
        return try {
            val deviceTypesSnapshot = deviceTypeCollection.get().await()
            val deviceTypes = mutableListOf<String>()

            for (document in deviceTypesSnapshot.documents) {
                val deviceType = document.toObject(DeviceType::class.java)!!
                deviceTypes.add(deviceType.type!!) // Ensure type is not null
            }

            deviceTypes
        } catch (e: Exception) {
            Log.e("Get device types", "Error fetching device types: $e")
            emptyList() // Return an empty list on error
        }
    }

    override suspend fun getAllDevices(): List<DeviceType> {
        return try {
            val deviceTypesSnapshot = deviceTypeCollection.get().await()
            val devices = mutableListOf<DeviceType>()

            for (document in deviceTypesSnapshot.documents) {
                val device = document.toObject(DeviceType::class.java)!!
                devices.add(device)
            }

            devices
        } catch (e: Exception) {
            Log.e("Get all devices", "Error fetching devices: $e")
            emptyList() // Return an empty list on error
        }
    }


    override suspend fun addIp(ip: Ip): TransactionResult {
        return try {
            val existingTypeQuery = ipsCollection.whereEqualTo("value", ip.value)
            val existingTypeSnapshot = existingTypeQuery.get().await()
            if (existingTypeSnapshot.documents.isNotEmpty()) {
                // Device type already exists
                return TransactionResult.Failure(Exception("ip already exists"))
            } else {
                // Add the new device type
                ipsCollection.add(ip).await()

                increaseDeviceNumber(ip.deviceType.toString())


                TransactionResult.Success() // Return Success directly


            }
        } catch (e: Exception) {
            Log.e("test add device type", e.message.toString())
            return TransactionResult.Failure(e)
        }
    }

    override suspend fun updateIP(
        id: String,
        updatedIp: Ip,
        oldDeviceType: String
    ): TransactionResult {
        return try {
            // 1. Fetch existing IP document and check if it exists
            val querySnapshot = ipsCollection
                .whereEqualTo("id", id)
                .get()
                .await()

            if (querySnapshot.documents.isEmpty()) {
                return TransactionResult.Failure(Exception("IP with ID $id not found"))
            }

            // 2. Check for existing IP (only if value is changing)
            if (updatedIp.value != querySnapshot.documents.first()["value"]) {
                val existingIpQuery = ipsCollection.whereEqualTo("value", updatedIp.value)
                    .get()
                    .await()
                if (existingIpQuery.documents.isNotEmpty() && existingIpQuery.documents.first()["id"] != id) {
                    return TransactionResult.Failure(Exception("ip already exists"))
                }
            }

            // 3. Update IP document
            val ipDocument = querySnapshot.documents.first()
            ipDocument.reference.set(updatedIp.copy(id = id)).await()

            // 4. Update device type counters (if device type changed)
            if (updatedIp.deviceType != querySnapshot.documents.first()["deviceType"]) {
                decreaseDeviceNumber(oldDeviceType)
                updatedIp.deviceType?.let { increaseDeviceNumber(it) }
            }

            // 5. Update parent IPs (if IP value changed)
            val originalIpValue = querySnapshot.documents.first()["value"] as String?
            if (updatedIp.value != originalIpValue) {
                updateParentIp(originalIpValue!!, updatedIp.value!!)
            }

            TransactionResult.Success()

        } catch (e: Exception) {
            Log.e("updateIP", "Error updating IP: $e")
            TransactionResult.Failure(e)
        }
    }


    private suspend fun updateParentIp(oldParentIp: String?, newParentIp: String) {
        try {
            val subListQuery = ipsCollection.whereEqualTo("parentIp", oldParentIp)
            val subListSnapshot = subListQuery.get().await()

            for (document in subListSnapshot.documents) {
                document.reference.update("parentIp", newParentIp).await()
            }
        } catch (e: Exception) {
            Log.e("Update Parent IP", "Error updating parent IPs: $e")
        }
    }


    override suspend fun increaseDeviceNumber(deviceName: String): TransactionResult {
        return try {
            // Query to find the document with the matching device type
            val querySnapshot = deviceTypeCollection
                .whereEqualTo("type", deviceName)
                .get()
                .await()

            if (querySnapshot.documents.isEmpty()) {
                // DeviceType document not found
                return TransactionResult.Failure(Exception("DeviceType with type $deviceName not found"))
            }

            val deviceTypeDocument = querySnapshot.documents.first()
            val currentCounter = deviceTypeDocument.getLong("counter")
            val newCounter = currentCounter?.plus(1)

            // Update the document with the new counter value
            deviceTypeDocument.reference.update("counter", newCounter).await()

            TransactionResult.Success()

        } catch (e: Exception) {
            TransactionResult.Failure(e)
        }
    }


    override suspend fun getMainIPs(): List<Ip> {
        return try {
            val mainIPsQuery = ipsCollection.whereEqualTo("parentIp", null)
            val mainIPsSnapshot = mainIPsQuery
                .orderBy("date", Query.Direction.DESCENDING)
                .get().await()

            val mainIPs = mutableListOf<Ip>()
            for (document in mainIPsSnapshot.documents) {
                val ip = document.toObject(Ip::class.java)!!
                mainIPs.add(ip)
            }

            mainIPs
        } catch (e: Exception) {
            Log.e("Get main IPs", "Error fetching main IPs: $e")
            emptyList() // Return an empty list on error
        }
    }

    override suspend fun getSubListIPs(parentIp: String): List<Ip> {
        return try {
            val subListQuery = ipsCollection.whereEqualTo("parentIp", parentIp)
            val subListSnapshot = subListQuery.get().await()

            val subListIPs = mutableListOf<Ip>()
            for (document in subListSnapshot.documents) {
                val ip = document.toObject(Ip::class.java)!!
                subListIPs.add(ip)
            }

            subListIPs
        } catch (e: Exception) {
            Log.e("Get sub list IPs", "Error fetching sub list IPs: $e")
            emptyList()
        }
    }

    override suspend fun getSpecificIp(id: String): Ip {
        try {
            val querySnapshot = ipsCollection.whereEqualTo("id", id)
                .get().await()

            if (querySnapshot.documents.isEmpty()) {
                // Task not found
                throw Exception("Task with ID $id not found")
            }

            val ipDocument = querySnapshot.documents.first()
            val ip = ipDocument.toObject(Ip::class.java)!!
            Log.e("Get Ip by ID", "Error getting task: ${ip.parentIp}")
            return ip
        } catch (e: Exception) {
            Log.e("Get Ip by ID", "Error getting task: $e")
            throw e
        }
    }


    override suspend fun deleteIp(ip: Ip, value: String): TransactionResult {
        return try {
            // Delete the IP document
            val querySnapshot = ipsCollection.whereEqualTo("id", ip.id).get().await()
            val ipDocument = querySnapshot.documents.first()
            ipDocument.reference.delete().await()

            // Search for IPs with the specified parentIp and set it to null
            val subListQuery = ipsCollection.whereEqualTo("parentIp", value)
            val subListSnapshot = subListQuery.get().await()

            for (document in subListSnapshot.documents) {
                val subListIpId = document.id
                ipsCollection.document(subListIpId).update("parentIp", null).await()
            }
            decreaseDeviceNumber(ip.deviceType!!)

            TransactionResult.Success()
        } catch (e: Exception) {
            Log.e("Delete IP", "Error deleting IP: $e")
            TransactionResult.Failure(e)
        }
    }

    override suspend fun decreaseDeviceNumber(deviceName: String): TransactionResult {
        return try {
            // Query to find the document with the matching device type
            val querySnapshot = deviceTypeCollection
                .whereEqualTo("type", deviceName)
                .get()
                .await()

            if (querySnapshot.documents.isEmpty()) {
                // DeviceType document not found
                return TransactionResult.Failure(Exception("DeviceType with type $deviceName not found"))
            }

            val deviceTypeDocument = querySnapshot.documents.first()
            val currentCounter = deviceTypeDocument.getLong("counter") ?: 0
            var newCounter = currentCounter - 1

            if (newCounter < 0) {
                newCounter = 0
            }

            // Update the document with the new counter value
            deviceTypeDocument.reference.update("counter", newCounter).await()

            TransactionResult.Success()

        } catch (e: Exception) {
            TransactionResult.Failure(e)
        }
    }


    override suspend fun searchIp(query: String): List<Ip> {
        return try {
            val searchResult = mutableListOf<Ip>()

            val searchQuery = ipsCollection
                .whereGreaterThanOrEqualTo("value", query)
                .whereLessThanOrEqualTo("value", query + "\uf8ff")
                .get()
                .await()

            // Add matching results to the list
            for (document in searchQuery.documents) {
                val ip = document.toObject(Ip::class.java)
                ip?.let { searchResult.add(it) }
            }

            // If there are no exact matches in 'value', check 'description'
            if (searchResult.isEmpty()) {
                val descriptionQuery = ipsCollection
                    .whereGreaterThanOrEqualTo("description", query)
                    .whereLessThanOrEqualTo("description", query + "\uf8ff")
                    .get()
                    .await()

                // Add matching results to the list
                for (document in descriptionQuery.documents) {
                    val ip = document.toObject(Ip::class.java)
                    ip?.let { searchResult.add(it) }
                }
            }

            // If there are still no matches, check 'keyword'
            if (searchResult.isEmpty()) {
                val keywordQuery = ipsCollection
                    .whereGreaterThanOrEqualTo("keyword", query)
                    .whereLessThanOrEqualTo("keyword", query + "\uf8ff")
                    .get()
                    .await()

                // Add matching results to the list
                for (document in keywordQuery.documents) {
                    val ip = document.toObject(Ip::class.java)
                    ip?.let { searchResult.add(it) }
                }
            }

            // Return the list of matching IPs
            searchResult
        } catch (e: Exception) {
            Log.e("test searchIp", e.message.toString())
            emptyList() // Return an empty list in case of an error
        }
    }


}