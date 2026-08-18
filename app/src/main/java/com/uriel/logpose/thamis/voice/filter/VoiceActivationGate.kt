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
        val hotwordsLog = setOf(
            "log", "lujo", "lodge", "los", "logg", "lock", "loc", "look", "block", "bloc", "local", "lo", "yo", "no", "now", "hola", "ola", "rog", "dojo", "dog", "doc", "long",
            "lord", "low", "love", "loop", "road", "rock", "lote", "lone"
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
            
            // v10.9.9: Blindaje contra falsos positivos de "lo pone" (Exigir longitud mínima de payload si es 'lo')
            if (firstToken == "lo" && nextToken.startsWith("pone")) {
                if (tokens.size < 3) {
                    LogPoseLogger.d("Gate", "Bloqueo preventivo: 'lo pone' sin payload suficiente.")
                    return false
                }
            }

            val isValidContext = imperativeRoots.any { nextToken.startsWith(it) }
            
            if (isValidContext) {
                LogPoseLogger.i("Gate", "Contextual Wake-Word Detection: '$firstToken' -> valid context ('$nextToken')")
                return true
            }
            return false
        }

        val otherTriggers = listOf(
            "pone", "pon", "sube", "baja", "pausa", "siguiente", "anterior", 
            "llama", "llamá", "llevarme", "navegar", "abrí", "abri", "reproducir", "vamos", "vamo", "vam",
            "para", "detener", "mandá", "mandame", "escribí", "tan", "uzbekistan", "duki", "anuel", "renga", "luck",
            "buscá", "busca", "sacá", "cancel", "cancela", "viaje", "estado", "consultar", "entrena", "entrenamiento", "simula", "simulación",
            "inicia", "iniciar", "empeza", "empezá", "empezar", "activa", "activar"
        )

        return firstToken == "log" || otherTriggers.any { lowerText.contains(it) }
    }

    fun destroy() {
        expirationJob?.cancel()
        gateScope.cancel()
    }
}
