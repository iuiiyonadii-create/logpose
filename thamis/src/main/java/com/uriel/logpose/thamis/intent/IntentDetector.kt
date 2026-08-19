package com.uriel.logpose.thamis.intent

import android.net.Uri
import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Motor de Detección de Intenciones "Argentum Staff" (v71.2).
 * v71.2: Blindaje de Acero contra falsas activaciones (Eliminación de palabras 'lenient').
 */
object IntentDetector {

    private val CLEAN_TEXT_REGEX = Regex("[^a-z0-9ñáéíóú ]")

    // v72.0: Radar de Wake-word robusto (Alineado con ThamisAssistant)
    private val WAKE_WORDS = setOf(
        "log", "logpose", "lo", "los", "la", "no", "look", "lujo", "blog", "ploc", "box", "fox"
    )

    private val MAPEO_FONETICO = mapOf(
        "habría" to "abrí",
        "pne" to "poné",
        "pone" to "poné",
        "pony" to "poné",
        "poni" to "poné",
        "pongo" to "poné",
        "poneme" to "poné",
        "ponele" to "poné",
        "pon" to "poné",
        "poner" to "poné",
        "yama" to "llamá",
        "llama" to "llamá",
        "llamame" to "llamá",
        "abri" to "abrí",
        "wasa" to "whatsapp",
        "buscame" to "buscá",
        "uzbeguistan" to "uzbekistán",
        "uzbeguistán" to "uzbekistán",
        "voz únicos" to "babasónicos",
        "voz unicos" to "babasónicos",
        "huequito" to "babasónicos"
    )

    private val RAICES_DISPARADORES = linkedMapOf(
        Intent.CALL_CONTACT to listOf("llam", "disc", "fon"),
        Intent.SEND_MESSAGE to listOf("mensaj", "escrib", "mand", "envi", "redact", "decil"),
        Intent.OPEN_APP to listOf("abri", "lanz", "app", "metet", "meté", "larg"),
        Intent.NAVIGATE to listOf("vam", "ir", "lleva", "anda", "andá", "guiam", "lleg", "busc", "viaje"),
        Intent.PLAY_MUSIC to listOf("pon", "poné", "escucha", "reproduci", "reproduce", "play"),
        Intent.SAFETY_ALERT to listOf("ojo", "guard", "cuid", "atent"),
        Intent.VEHICLE_STATUS to listOf("como", "esta", "temp", "naft", "nafta"),
        Intent.WEATHER to listOf("clima", "llover", "pronostico", "sol", "temp", "temperat"),
        Intent.CONFIRM_ACTION to listOf("entrena", "simula", "si", "ok", "dale"),
        Intent.CANCEL_ACTION to listOf("deten", "para", "no", "cancel")
    )

    fun detect(text: String, ignoreWakeWord: Boolean = false): DetectionResult {
        val learnedCorrection = com.uriel.logpose.thamis.learning.LearningEngine.getPhoneticCorrection(text.lowercase())
        var workingText = (learnedCorrection ?: text).lowercase().trim()
        var containsHotword = false

        // 1. WAKE-WORD STRIPPER ESTRICTO
        val tokens = workingText.split("\\s+".toRegex()).toMutableList()
        if (tokens.isNotEmpty()) {
            val firstToken = tokens[0]
            if (firstToken in WAKE_WORDS) {
                containsHotword = true
                tokens.removeAt(0)
            }
        }

        // 2. MAPEO FONÉTICO TRADICIONAL
        for (entry in MAPEO_FONETICO) {
            val error = entry.key
            val correccion = entry.value
            val regex = Regex("\\b${Regex.escape(error)}\\b", RegexOption.IGNORE_CASE)
            if (regex.containsMatchIn(workingText)) {
                workingText = workingText.replace(regex, correccion)
            }
        }

        if (!containsHotword && !ignoreWakeWord) return DetectionResult(Intent.UNKNOWN, 0f, type = "PRIVACY_MUTED")

        val remainingText = tokens.joinToString(" ")
        return clasificarArgentum(remainingText) ?: DetectionResult(Intent.UNKNOWN, 0.1f)
    }

    fun isActionVerbProxy(token: String): Boolean {
        return isActionVerb(token)
    }

    private fun isActionVerb(token: String): Boolean {
        val dna = com.uriel.logpose.thamis.normalizer.LanguageNormalizer.normalizeToDNA(token)
        return RAICES_DISPARADORES.values.flatten().any { root ->
            val rootDna = com.uriel.logpose.thamis.normalizer.LanguageNormalizer.normalizeToDNA(root)
            dna.startsWith(rootDna) 
        }
    }

    private fun clasificarArgentum(textWithoutWake: String): DetectionResult? {
        val acousticGarbage = setOf("is", "que", "y estan", "y están", "estan", "están", "viste", "che", "fue", "estos")
        val preCleaned = textWithoutWake.lowercase().split(" ").filter { it !in acousticGarbage }.joinToString(" ").trim()

        if (preCleaned.isEmpty()) return null

        val palabrasDNA = preCleaned.split("\\s+".toRegex()).filter { it.isNotBlank() }
            .map { com.uriel.logpose.thamis.normalizer.LanguageNormalizer.normalizeToDNA(it) }
        
        var intentDetectado: Intent? = null
        var indexGanador = -1

        for (entry in RAICES_DISPARADORES) {
            val intent = entry.key
            val raices = entry.value
            for (raiz in raices) {
                val raizDNA = com.uriel.logpose.thamis.normalizer.LanguageNormalizer.normalizeToDNA(raiz)
                val matchIndex = palabrasDNA.indexOfFirst { it.startsWith(raizDNA) }
                if (matchIndex != -1) {
                    intentDetectado = intent
                    indexGanador = matchIndex
                    break
                }
            }
            if (intentDetectado != null) break
        }

        if (intentDetectado == null) return null

        val originalWords = preCleaned.split("\\s+".toRegex()).filter { it.isNotBlank() }
        val tokensPayload = originalWords.filterIndexed { i, _ -> i != indexGanador }
        var payloadFinal = tokensPayload.joinToString(" ").trim()
        
        if (intentDetectado == Intent.PLAY_MUSIC) {
            val isKnownEntity = com.uriel.logpose.core.services.MusicNormalizerDelegator.isKnown(payloadFinal)
            if (payloadFinal.length < 3 && !isKnownEntity && payloadFinal.lowercase() != "play") return null
        }

        val isKnown = com.uriel.logpose.core.services.MusicNormalizerDelegator.isKnown(payloadFinal)
        val entities = mutableMapOf("parameter" to payloadFinal)
        var intentUri: String? = null

        if (intentDetectado == Intent.PLAY_MUSIC) {
            entities["media"] = payloadFinal
            intentUri = "spotify:search:" + Uri.encode(payloadFinal)
        } else if (intentDetectado == Intent.NAVIGATE) {
            entities["destination"] = payloadFinal
            intentUri = "geo:0,0?q=" + Uri.encode(payloadFinal)
        }

        return DetectionResult(intentDetectado!!, if (isKnown) 0.98f else 0.85f, entities, intentUri, "STAFF_MATCH")
    }

    data class DetectionResult(
        val intent: Intent,
        val score: Float,
        val entities: Map<String, String> = emptyMap(),
        val intentUri: String? = null,
        val type: String = "UNKNOWN"
    )
}
