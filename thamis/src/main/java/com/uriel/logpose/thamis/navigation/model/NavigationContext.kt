package com.uriel.logpose.thamis.navigation.model

/**
 * Representa el estado del mundo relevante para las decisiones de navegación.
 */
data class NavigationContext(
    val gpsAvailable: Boolean,
    val currentLocation: String?, // Representación simplificada
    val activeRoute: String?,
    val destination: String?,
    val speedKmh: Int
)
