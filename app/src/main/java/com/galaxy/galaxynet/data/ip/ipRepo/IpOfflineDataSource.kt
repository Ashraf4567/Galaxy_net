package com.galaxy.galaxynet.data.ip.ipRepo

import com.galaxy.galaxynet.data.local.IpDao
import com.galaxy.galaxynet.model.Ip
import com.galaxy.util.toIp
import com.galaxy.util.toIpEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class IpOfflineDataSource @Inject constructor(
    private val ipDao: IpDao
): OfflineIpRepo {
    override fun getMainIPs(): Flow<List<Ip>> = ipDao.getMainIps().map { entities->
        entities.map { it.toIp() }
    }

    override fun getByParentIp(parentIp: String): Flow<List<Ip>> = ipDao.getByParentIp(parentIp)
        .map { entities->
        entities.map { it.toIp() }
    }

    override suspend fun insertAll(ips: List<Ip>) {
        ipDao.insertAll(ips.map { it.toIpEntity() })
    }
}