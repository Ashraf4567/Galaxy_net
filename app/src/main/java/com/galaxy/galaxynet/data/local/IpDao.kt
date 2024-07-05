package com.galaxy.galaxynet.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.galaxy.galaxynet.model.Ip
import com.galaxy.galaxynet.model.IpEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IpDao {
    @Query("SELECT * FROM IpEntity")
    fun getAll(): Flow<List<IpEntity>>

    @Query("SELECT * FROM IpEntity WHERE parentIp = :parentIp")
    fun getByParentIp(parentIp: String): Flow<List<IpEntity>>

    @Query("SELECT * FROM IpEntity WHERE parentIp = null")
    fun getMainIps(): Flow<List<IpEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ips: List<IpEntity>)
}