package com.uriel.logpose.domain.models

data class LogPoseDevice(

    val mac: String,

    val name: String,

    val type: DeviceType,

    val connected: Boolean = false

)