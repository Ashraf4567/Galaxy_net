package com.galaxy.galaxynet.data.ip.ipRepo

import com.galaxy.galaxynet.model.Ip
import kotlinx.coroutines.flow.Flow

interface OfflineIpRepo {
    fun getMainIPs(): Flow<List<Ip>>
    fun getByParentIp(parentIp: String): Flow<List<Ip>>
    suspend fun insertAll(ips: List<Ip>)
}