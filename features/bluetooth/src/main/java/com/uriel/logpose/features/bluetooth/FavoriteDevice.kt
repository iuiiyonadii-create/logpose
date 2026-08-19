package com.uriel.logpose.features.bluetooth

data class FavoriteDevice(

    val mac: String,

    val name: String,

    val connected: Boolean = false

)