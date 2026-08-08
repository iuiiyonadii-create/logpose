package com.uriel.logpose.thamis.intent

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.knowledge.KnowledgeBase
import com.uriel.logpose.thamis.knowledge.KnowledgeRule
import com.uriel.logpose.thamis.language.LanguageProcessor
import com.uriel.logpose.thamis.language.PhoneticEngine
import com.uriel.logpose.thamis.language.advanced.AdvancedLanguageEngine
import com.uriel.logpose.thamis.language.advanced.SpanishMetaphoneRioplatense
import com.uriel.logpose.thamis.learning.LearningEngine
import com.uriel.logpose.thamis.world.engine.WorldModelEngine
import com.uriel.logpose.thamis.world.model.WorldSnapshot
import com.uriel.logpose.thamis.world.model.RiskLevel
import com.thamis.lab.core.contracts.intent.Intent

/**
 * Detecta la intención principal del usuario utilizando el Cognitive Pipeline unificado.
 * Optimización V4 (Misión #015): Adaptive NLU Overlay (Prioridad de Aprendizaje).
 */
object IntentDetector {

    private const val MIN_SIMILARITY_THRESHOLD = 0.50f

    fun detect(text: String): DetectionResult {
        val snapshot = WorldModelEngine.getCurrentSnapshot()
        val conversationState = snapshot.cognitive.conversationState
        
        if (conversationState == "WAITING_MESSAGE_CONTENT") {
            return DetectionResult(Intent.MESSAGE_CONTENT, 1.0f, mapOf("content" to text))
        }

        val processedText = LanguageProcessor.process(text)

        // Misión #022.3: Bloqueo de Dominio por Verbo (v4.6)
        val lowerText = processedText.lowercase()
        val musicTriggers = setOf("pone", "poneme", "poné", "poner", "reproduce", "reproduci", "reproducir", "play", "pasame", "escuchar", "sonar", "tira", "tirame")
        val isMusicVerb = musicTriggers.any { lowerText.startsWith(it) }
        
        if (conversationState == "WAITING_CONFIRMATION") {
            if (isConfirmation(processedText)) return DetectionResult(Intent.CONFIRM_ACTION, 1.0f)
            if (isCancellation(processedText)) return DetectionResult(Intent.CANCEL_ACTION, 1.0f)
        }
        
        // --- NIVEL 1: OVERLAY ADAPTATIVO ---
        val learnedIntent = LearningEngine.getLearnedIntent(processedText)
        if (learnedIntent != null) {
            LogPoseLogger.i("IntentDetector: Hit en Overlay Personal -> $learnedIntent")
            return DetectionResult(learnedIntent, 1.0f)
        }

        // --- NIVEL 1.5: ATAJOS RÁPIDOS Y ENTIDADES PURAS (ALF-R v4.5) ---
        val tokens = processedText.split(" ")
        val resolverResult = com.uriel.logpose.core.app.LogPoseApplication.instance.contextualResolver.resolve(tokens)
        
        if (resolverResult != null && resolverResult.confidenceScore > 0.8f) {
            LogPoseLogger.i("IntentDetector: ALF-R v4.5 Match -> ${resolverResult.intent} (Conf: ${resolverResult.confidenceScore})")
            val entities = mutableMapOf<String, String>()
            when (resolverResult.intent) {
                Intent.PLAY_MUSIC -> {
                    entities["media"] = resolverResult.cleanedPayload
                    if (!com.uriel.logpose.features.voice.MusicVocabulary.isKnown(resolverResult.cleanedPayload)) {
                        LearningEngine.learnMusicEntity(resolverResult.cleanedPayload)
                    }
                }
                Intent.CALL_CONTACT, Intent.SEND_MESSAGE -> entities["contact"] = resolverResult.matchedAnchor.rawText
                Intent.NAVIGATE -> entities["destination"] = resolverResult.cleanedPayload
                Intent.OPEN_APP -> {
                    // Si es un verbo musical, prohibimos saltar a OPEN_APP (v4.6)
                    if (isMusicVerb) {
                        LogPoseLogger.w("IntentDetector: Intento de secuestro OPEN_APP bloqueado por Verbo Musical.")
                        return DetectionResult(Intent.PLAY_MUSIC, 0.9f, mapOf("media" to resolverResult.cleanedPayload))
                    }
                    entities["app_name"] = resolverResult.matchedAnchor.rawText
                }
                else -> {}
            }
            return DetectionResult(resolverResult.intent, resolverResult.confidenceScore, entities)
        }

        val regionalBoost = if (text.contains("che") || text.contains("boludo") || text.contains("posta")) 0.1f else 0.0f

        // --- NIVEL 2: BÚSQUEDA INDEXADA v4.0 (ALF-R Unificado) ---
        val noiseLevel = snapshot.systems.audio.noiseLevel
        val semanticCloud = buildSemanticCloud(snapshot)
        
        val metaInput = PhoneticEngine.getPhoneticKey(processedText, noiseLevel)
        val metaTokens = tokens.map { PhoneticEngine.getPhoneticKey(it, noiseLevel) }

        val candidates = mutableSetOf<KnowledgeRule>()
        KnowledgeBase.phoneticIndex[metaInput]?.let { candidates.addAll(it) }
        metaTokens.forEach { tokenMeta ->
            KnowledgeBase.phoneticIndex[tokenMeta]?.let { candidates.addAll(it) }
        }

        val rulesToScan = if (candidates.isEmpty()) KnowledgeBase.entries else candidates

        var bestIntent = Intent.UNKNOWN
        var maxScore = 0f

        val isMoving = snapshot.vehicle.moving
        val isNavigating = snapshot.systems.navigation.isNavigating
        val isCallActive = snapshot.systems.communication.isCallActive

        for (rule in rulesToScan) {
            var contextBoost = 1.0f
            when (rule.intent) {
                Intent.CALL_CONTACT -> { if (!isMoving) contextBoost += 0.2f; if (isCallActive) contextBoost -= 0.5f }
                Intent.NAVIGATE -> { if (isMoving) contextBoost += 0.15f; if (isNavigating) contextBoost += 0.25f }
                Intent.ANSWER_CALL -> if (isCallActive) contextBoost += 0.8f
                Intent.STOP_NAVIGATION -> if (isNavigating) contextBoost += 0.5f
                Intent.PLAY_MUSIC -> if (snapshot.systems.audio.isPlaying) contextBoost += 0.1f
                else -> {}
            }
            
            for ((phrase, metaTarget) in rule.phoneticKeys) {
                val similarityScore = AdvancedLanguageEngine.getSimilarityWithMetas(
                    processedText, metaInput, phrase, metaTarget, noiseLevel, semanticCloud
                )
                
                // Bonus Staff: Si la primera palabra (verbo) coincide fonéticamente, gran boost
                val firstWordInput = metaInput.split(" ").firstOrNull() ?: ""
                val firstWordTarget = metaTarget.split(" ").firstOrNull() ?: ""
                val verbBoost = if (firstWordInput == firstWordTarget && firstWordInput.length > 2) 0.15f else 0.0f

                val combinedScore = (similarityScore + verbBoost) * contextBoost
                if (combinedScore > maxScore) {
                    maxScore = combinedScore
                    bestIntent = rule.intent
                }
            }
        }

        val finalScore = ((maxScore + regionalBoost) * LearningEngine.getHabitWeight(bestIntent)).coerceAtMost(1.0f)
        val finalIntent = if (finalScore >= MIN_SIMILARITY_THRESHOLD) bestIntent else Intent.UNKNOWN

        val entities = mutableMapOf<String, String>()
        if (finalIntent == Intent.SEND_MESSAGE || finalIntent == Intent.CALL_CONTACT) {
             entities["contact"] = processedText
                .replace(Regex("(?i)^llamar a |^llamá a |^llama a |^mensaje a |^mandale un mensaje a |^mandale mensaje a |^mandale a |^decile a "), "")
                .trim()
        }

        return DetectionResult(finalIntent, finalScore, entities)
    }

    private fun isConfirmation(text: String): Boolean {
        val matches = setOf("si", "sí", "ok", "dale", "mandalo", "enviar", "confirmar", "confirmado", "obvio", "de una", "joya")
        return matches.any { text.contains(it) }
    }

    private fun isCancellation(text: String): Boolean {
        val matches = setOf("no", "cancelar", "cancela", "parar", "detener", "espera", "ni en pedo", "ni a palos", "nones")
        return matches.any { text.contains(it) }
    }

    /**
     * Construye una Nube Semántica (Semantic Cloud) v4.0 basada en el estado del mundo.
     * Los tokens resultantes reciben un boost de probabilidad fonética.
     */
    private fun buildSemanticCloud(snapshot: WorldSnapshot): Map<String, Float> {
        val cloud = mutableMapOf<String, Float>()
        
        // 1. Contexto de Movimiento (Rápido -> Seguridad y Clima)
        if (snapshot.vehicle.speedKmh > 80) {
            cloud["velocidad"] = 0.3f
            cloud["clima"] = 0.2f
            cloud["parar"] = 0.2f
        }

        // 2. Contexto Multimedia (Spotify activo -> Control de música)
        if (snapshot.systems.audio.isPlaying) {
            cloud["siguiente"] = 0.25f
            cloud["pasala"] = 0.2f
            cloud["pausa"] = 0.15f
            cloud["volumen"] = 0.15f
        }

        // 3. Contexto de Navegación (Llegando o Perdido -> Ubicación)
        if (snapshot.systems.navigation.isNavigating) {
            cloud["donde"] = 0.2f
            cloud["falta"] = 0.2f
            cloud["cancelar"] = 0.15f
        }

        return cloud
    }
}
