package com.galaxy.galaxynet.data.ipRepo

import com.galaxy.util.TransactionResult

interface IpRepository {
    suspend fun addDeviceType(type: String): TransactionResult
}