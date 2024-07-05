package com.galaxy.galaxynet.data.ip.ipRepo

import com.galaxy.galaxynet.model.DeviceType
import com.galaxy.galaxynet.model.Ip
import com.galaxy.util.Resource
import com.galaxy.util.TransactionResult
import kotlinx.coroutines.flow.Flow

interface IpRepository {
    suspend fun addDeviceType(type: String): TransactionResult
    suspend fun getAllDevicesTypes(): List<String>
    suspend fun getAllIps(): List<Ip>
    suspend fun addIp(ip: Ip): TransactionResult
    fun getMainIPs(): Flow<Resource<List<Ip>>>
    suspend fun getAllDevices(): List<DeviceType>
    suspend fun getSubListIPs(parentIp: String): Flow<Resource<List<Ip>>>
    suspend fun deleteIp(ip: Ip, value: String): TransactionResult

    suspend fun getSpecificIp(id: String): Ip
    suspend fun updateDeviceTypeCounters(): TransactionResult

    suspend fun increaseDeviceNumber(deviceName: String): TransactionResult
    suspend fun decreaseDeviceNumber(deviceName: String): TransactionResult
    suspend fun updateIP(id: String, updatedIp: Ip, oldDeviceName: String): TransactionResult
    suspend fun getIpsByDevice(deviceName: String): List<Ip>
    suspend fun changeDeviceName(oldDeviceName: String, newDeviceName: String): TransactionResult

}