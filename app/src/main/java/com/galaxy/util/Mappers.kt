package com.galaxy.util

import com.galaxy.galaxynet.model.Ip
import com.galaxy.galaxynet.model.IpEntity

fun IpEntity.toIp() = Ip(
    value,
    description,
    deviceType,
    ownerName,
    parentIp,
    date,
    keyword,
    id
)

fun Ip.toIpEntity() = IpEntity(
    value,
    description,
    deviceType,
    ownerName,
    parentIp,
    date,
    keyword,
    id?:""
)