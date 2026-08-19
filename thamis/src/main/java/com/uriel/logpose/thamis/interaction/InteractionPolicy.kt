package com.uriel.logpose.thamis.interaction

/**
 * Define las reglas dinámicas de planificación.
 */
object InteractionPolicy {

    enum class DrivingMode {
        NORMAL,     // Balanceado
        DELIVERY,   // Prioridad a notificaciones y mensajes
        HIGHWAY,    // Prioridad a navegación y emergencias (bloquea multimedia)
        CITY,       // Prioridad a sensores y tráfico
        SILENCE,    // Solo emergencias
        CUSTOM      // Definido por el usuario
    }

    var currentMode: DrivingMode = DrivingMode.NORMAL

    /**
     * Ajusta la prioridad efectiva basada en el modo de conducción.
     */
    fun calculateEffectivePriority(request: InteractionRequest): Int {
        val base = request.priority.level
        return when (currentMode) {
            DrivingMode.SILENCE -> if (request.priority == InteractionPriority.EMERGENCY) base else 0
            DrivingMode.HIGHWAY -> {
                if (request.domain == InteractionRequest.Domain.MULTIMEDIA) base - 20
                else if (request.domain == InteractionRequest.Domain.NAVIGATION) base + 10
                else base
            }
            DrivingMode.DELIVERY -> {
                if (request.domain == InteractionRequest.Domain.NOTIFICATION) base + 20
                else base
            }
            else -> base
        }
    }

    /**
     * Determina el cooldown de silencio después de hablar.
     */
    fun getPostInteractionSilenceMs(): Long {
        return when (currentMode) {
            DrivingMode.HIGHWAY -> 5000L
            DrivingMode.DELIVERY -> 1500L
            else -> 3000L
        }
    }
}
