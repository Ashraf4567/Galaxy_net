package com.galaxy.galaxynet.model

data class DeviceType(
    val type: String? = null,
    val counter: Int = 0
) {
    companion object {
        const val DEVICE_TYPE_COLLECTION_NAME = "Device type list"

    }
}
