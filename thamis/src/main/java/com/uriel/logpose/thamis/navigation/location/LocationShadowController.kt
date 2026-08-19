package com.uriel.logpose.thamis.navigation.location

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Controlador en modo Shadow para observar y registrar la inteligencia de ubicación.
 */
object LocationShadowController {
    private val traces = mutableListOf<LocationTrace>()

    fun process(input: String, location: CurrentLocation, gpsAvailable: Boolean, isMoving: Boolean) {
        val intent = LocationResolver.resolve(input)
        val confidence = LocationValidation.calculateConfidence(gpsAvailable, location.gpsAccuracy, isMoving)
        val response = LocationFormatter.formatResponse(intent, location, gpsAvailable)

        val trace = LocationTrace(
            input = input,
            intent = intent,
            confidence = confidence,
            gpsAvailable = gpsAvailable,
            accuracy = location.gpsAccuracy,
            response = response
        )

        traces.add(trace)
        if (traces.size > 100) traces.removeAt(0)

        LogPoseLogger.i("[THAMIS_LOCATION] Shadow Mode - Intent: $intent, Confidence: $confidence, GPS: $gpsAvailable, Response: $response")
    }

    fun getTraces(): List<LocationTrace> = traces.toList()
}
