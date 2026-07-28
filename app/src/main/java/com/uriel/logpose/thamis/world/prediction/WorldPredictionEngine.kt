package com.uriel.logpose.thamis.world.prediction

import com.uriel.logpose.thamis.world.model.WorldSnapshot

/**
 * Motor de predicción para anticipar estados futuros basados en el historial.
 */
object WorldPredictionEngine {

    fun predict(history: List<WorldSnapshot>): WorldPrediction {
        // Lógica simplificada para v1.0
        return WorldPrediction(
            probableArrivalMs = 0,
            probableStop = false,
            probableBluetoothLoss = false
        )
    }
}

data class WorldPrediction(
    val probableArrivalMs: Long,
    val probableStop: Boolean,
    val probableBluetoothLoss: Boolean
)
