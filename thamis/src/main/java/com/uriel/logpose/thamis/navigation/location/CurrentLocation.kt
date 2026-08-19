package com.uriel.logpose.thamis.navigation.location

/**
 * Modelo inmutable para representar la ubicación actual sin dependencias de Android.
 */
data class CurrentLocation(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val street: String? = null,
    val number: String? = null,
    val city: String? = null,
    val province: String? = null,
    val country: String? = null,
    val speed: Float = 0f,
    val gpsAccuracy: Float = 0f,
    val timestamp: Long = System.currentTimeMillis()
)
