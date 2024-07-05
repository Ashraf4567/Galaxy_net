package com.galaxy.galaxynet.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.galaxy.galaxynet.model.Ip
import com.galaxy.galaxynet.model.IpEntity

@Database(entities = [IpEntity::class] , version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun ipDao(): IpDao
}