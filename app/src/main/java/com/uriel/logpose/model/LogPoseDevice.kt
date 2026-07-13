package com.uriel.logpose.model

data class LogPoseDevice(

    val mac: String,

    val name: String,

    val type: DeviceType,

    val connected: Boolean = false

)