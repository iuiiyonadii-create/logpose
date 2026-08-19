package com.uriel.logpose.thamis.journey.provider

import com.uriel.logpose.thamis.journey.model.JourneyEvidence

/**
 * Interface del proveedor de datos para el motor de viaje.
 * Permite el desacoplamiento total de Android.
 */
interface JourneyDataProvider {
    fun getCurrentEvidences(): List<JourneyEvidence>
    fun isGPSAvailable(): Boolean
    fun getCurrentSpeed(): Float
    fun getStartLocation(): String?
}
