package com.galaxy.galaxynet.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Ip(
    val value: String? = null,
    val description: String? = null,
    val deviceType: String? = null,
    val ownerName: String? = null,
    val parentIp: String? = null,
    val date: String = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
    val keyword: String? = null,
    val id: String? = null
) {
    companion object {
        const val COLLECTION_NAME = "Ip List"
    }
}
