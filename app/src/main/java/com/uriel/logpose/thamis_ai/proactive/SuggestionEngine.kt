package com.uriel.logpose.thamis_ai.proactive

/**
 * Generates brief proactive suggestions.
 */
class SuggestionEngine {

    fun generate(action: ProactiveAction): String {
        return when (action) {
            ProactiveAction.SUGGEST -> "Siempre usás música al conectar el casco. ¿Querés activarla?"
            ProactiveAction.WARN -> "Batería baja. ¿Querés activar el modo ahorro?"
            ProactiveAction.REMIND -> "Tenés un destino próximo guardado."
            ProactiveAction.INFORM -> "Modo conducción activado correctamente."
        }
    }
}
