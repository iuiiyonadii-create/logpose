package com.uriel.logpose.thamis.voice.filter

import com.uriel.logpose.core.compat.core.LogPoseLogger
import kotlinx.coroutines.*
import kotlinx.coroutines.cancel

/**
 * THAMIS Voice Gate v1.1
 * Actúa como una barrera de activación inteligente.
 * Mantiene una ventana de sesión de 4s para permitir que el payload (ej: "uzbekistán", "duki")
 * pase sin ser bloqueado luego de un verbo de comando (ej: "pone").
 */
class VoiceActivationGate {

    private var lastTriggerTimestamp = 0L
    private val SESSION_WINDOW_MS = 4000L
    private var expirationJob: Job? = null
    private val gateScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    fun isSessionActive(): Boolean {
        return (System.currentTimeMillis() - lastTriggerTimestamp) < SESSION_WINDOW_MS
    }

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

        // 2. Confianza mínima de Vosk
        if (confidence < 0.50f) {
            LogPoseLogger.w("Gate: Bloqueado por baja confianza ($confidence)")
            return false
        }

        val currentTime = System.currentTimeMillis()
        val isSessionActive = (currentTime - lastTriggerTimestamp) < SESSION_WINDOW_MS

        // 3. Verificación de disparadores explícitos o sesión activa
        val hasExplicitIntent = containsCommandIntent(text)

        if (hasExplicitIntent) {
            lastTriggerTimestamp = currentTime
            planificarCierreAutomatico()
            LogPoseLogger.d("Gate: Disparador de intención detectado en '$text'")
            return true
        }

        if (isSessionActive) {
            LogPoseLogger.d("Gate: Aceptado por Ventana de Sesión Activa (${SESSION_WINDOW_MS}ms) -> '$text'")
            return true
        }

        LogPoseLogger.d("Gate: Bloqueado - No se detectó intención ni sesión activa en '$text'")
        return false
    }

    private fun planificarCierreAutomatico() {
        expirationJob?.cancel()
        expirationJob = gateScope.launch {
            delay(SESSION_WINDOW_MS)
            lastTriggerTimestamp = 0L
            LogPoseLogger.d("Gate: Ventana de 4s expirada de forma segura. Canal blindado.")
        }
    }

    private fun containsCommandIntent(text: String): Boolean {
        val lowerText = text.lowercase().trim()
        val tokens = lowerText.split("\\s+".toRegex())
        if (tokens.isEmpty()) return false

        val firstToken = tokens[0]
        // v82.6 STAFF: Reducción de ruido fonético. Eliminamos palabras comunes (no, yo, lo, para)
        // que causaban disparos accidentales en conversación normal.
        val hotwordsLog = setOf(
            "log", "logg", "lock", "loc", "look", "dojo", "dog", "doc", "long", "block", "bloc", "lujo", "lodge", "local", "rog", "lord", "loop"
        )
        
        // 🔒 RADAR ELÁSTICO v10.9.8: Validación contextual expandida para mutaciones por viento
        if (firstToken in hotwordsLog && firstToken != "log") {
            val nextToken = if (tokens.size > 1) tokens[1] else ""
            val casualConnectors = setOf("del", "que", "de", "un")
            
            // Protección de charla casual
            if (casualConnectors.contains(nextToken)) {
                LogPoseLogger.d("Gate: Mute temprano - Charla detectada ('$firstToken $nextToken')")
                return false
            }

            val imperativeRoots = setOf(
                "pone", "poné", "vamos", "vamo", "llevame", "lleva", "mandale", "mandame", "manda", "mandan",
                "escribile", "abri", "abrí", "ojo", "guarda", "buscá", "busca", "pon", "sacá", "reproduci", "reproduce"
            )
            
            val isValidContext = imperativeRoots.any { nextToken.startsWith(it) }
            
            if (isValidContext) {
                LogPoseLogger.i("Gate", "Contextual Wake-Word Detection: '$firstToken' -> valid context ('$nextToken')")
                return true
            }
            return false
        }

        // v82.6: Eliminamos otherTriggers globales para forzar el uso de la wake-word "Log"
        // fuera de la ventana de sesión activa.
        return firstToken == "log"
    }

    fun destroy() {
        expirationJob?.cancel()
        gateScope.cancel()
    }
}
