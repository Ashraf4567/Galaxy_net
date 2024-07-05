package com.galaxy.galaxynet.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
@Entity
data class IpEntity(
    val value: String? = null,
    val description: String? = null,
    val deviceType: String? = null,
    val ownerName: String? = null,
    val parentIp: String? = null,
    val date: String = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
    val keyword: String? = null,
    @PrimaryKey
    val id: String
)
