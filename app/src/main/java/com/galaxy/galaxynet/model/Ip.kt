package com.galaxy.galaxynet.model

data class Ip(
    val value: String? = null,
    val description: String? = null,
    val deviceType: String? = null
) {
    companion object {
        const val COLLECTION_NAME = "Ip List"
        const val DEVICE_TYPE_COLLECTION_NAME = "Device type"
    }
}
