package com.uriel.logpose.thamis.navigation

import com.uriel.logpose.thamis.navigation.model.NavigationDecision
import com.uriel.logpose.thamis.navigation.model.NavigationContext

/**
 * Valida la seguridad de una decisión de navegación antes de autorizarla.
 */
object NavigationSafetyGate {

    fun validate(decision: NavigationDecision, context: NavigationContext): Result {
        // 1. Bloqueo por GPS
        if (!context.gpsAvailable) return Result.REJECTED

        // 2. Bloqueo por Velocidad Extrema
        if (context.speedKmh > 120) return Result.BLOCKED

        // 3. Confirmación obligatoria a alta velocidad
        if (context.speedKmh > 100) return Result.CONFIRM_REQUIRED

        // 4. Destino desconocido
        if (decision.destination.isEmpty() || decision.destination == "UNKNOWN") return Result.CONFIRM_REQUIRED

        return Result.APPROVED
    }

    enum class Result {
        APPROVED,
        CONFIRM_REQUIRED,
        BLOCKED,
        REJECTED
    }
}
