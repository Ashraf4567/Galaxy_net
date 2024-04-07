package com.galaxy.galaxynet.data.ipRepo

import com.galaxy.galaxynet.model.DeviceType
import com.galaxy.galaxynet.model.Ip
import com.galaxy.util.TransactionResult

interface IpRepository {
    suspend fun addDeviceType(type: String): TransactionResult
    suspend fun getAllDevicesTypes(): List<String>
    suspend fun addIp(ip: Ip): TransactionResult
    suspend fun getMainIPs(): List<Ip>
    suspend fun getAllDevices(): List<DeviceType>
    suspend fun getSubListIPs(parentIp: String): List<Ip>
    suspend fun searchIp(query: String): List<Ip>
    suspend fun deleteIp(ip: Ip, value: String): TransactionResult

    suspend fun getSpecificIp(id: String): Ip
    suspend fun updateDeviceTypeCounters(): TransactionResult

    suspend fun increaseDeviceNumber(deviceName: String): TransactionResult
    suspend fun decreaseDeviceNumber(deviceName: String): TransactionResult
    suspend fun updateIP(id: String, updatedIp: Ip, oldDeviceName: String): TransactionResult

}