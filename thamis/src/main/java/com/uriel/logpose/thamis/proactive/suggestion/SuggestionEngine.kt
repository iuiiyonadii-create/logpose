package com.uriel.logpose.thamis.proactive.suggestion

import com.uriel.logpose.thamis.proactive.model.Suggestion
import com.uriel.logpose.thamis.world.model.WorldSnapshot

/**
 * Genera sugerencias proactivas basadas en el estado del mundo y patrones.
 */
object SuggestionEngine {

    fun generate(world: WorldSnapshot): List<Suggestion> {
        val suggestions = mutableListOf<Suggestion>()
        
        // Ejemplo: Batería baja
        if (world.systems.device.batteryPct < 20 && !world.systems.device.isCharging) {
            suggestions.add(Suggestion(
                message = "Tu batería está baja. Conectá el cargador cuando puedas.",
                benefit = "Evitar apagado del asistente",
                urgency = 70
            ))
        }

        return suggestions
    }
}
