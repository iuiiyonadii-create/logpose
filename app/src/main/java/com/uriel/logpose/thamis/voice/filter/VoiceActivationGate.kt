package com.uriel.logpose.thamis.voice.filter

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * THAMIS Voice Gate v1.0
 * Actúa como una barrera de activación para evitar que THAMIS procese ruido,
 * conversaciones de fondo o comandos incompletos.
 */
class VoiceActivationGate {

    fun shouldProcess(
        text: String,
        confidence: Float,
        noiseLevel: Float,
        audioDurationMs: Long
    ): Boolean {

        if (text.isBlank()) return false

        // 1. Filtrado por nivel de ruido (viento/motor fuerte)
        if (noiseLevel > 0.65f) {
            LogPoseLogger.w("Gate: Bloqueado por ruido excesivo ($noiseLevel)")
            return false
        }

        // 2. Filtrado por duración (evitar ruidos secos o sílabas sueltas)
        // Un comando normal en español suele durar más de 400ms
        if (audioDurationMs < 400) {
            LogPoseLogger.w("Gate: Bloqueado por duración insuficiente (${audioDurationMs}ms)")
            return false
        }

        // 3. Confianza mínima de Vosk
        if (confidence < 0.70f) {
            LogPoseLogger.w("Gate: Bloqueado por baja confianza ($confidence)")
            return false
        }

        // 4. Verificación de disparadores explícitos (Intención mínima)
        val hasIntent = containsCommandIntent(text)
        if (!hasIntent) {
            LogPoseLogger.d("Gate: Bloqueado - No se detectó intención en '$text'")
        }

        return hasIntent
    }

    private fun containsCommandIntent(text: String): Boolean {
        val triggers = listOf(
            "pone", "pon", "sube", "baja", "pausa", "siguiente", "anterior", 
            "llama", "llamá", "llevarme", "navegar", "abrí", "abri", "reproducir",
            "para", "detener", "mandá", "mandame", "escribí"
        )

        val lowerText = text.lowercase()
        return triggers.any { lowerText.contains(it) }
    }
}
