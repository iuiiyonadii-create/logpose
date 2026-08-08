package com.uriel.logpose.thamis.language

import android.util.Log
import com.uriel.logpose.core.app.LogPoseApplication
import com.uriel.logpose.core.parser.PhoneticDictionary
import com.uriel.logpose.features.voice.MusicVocabulary
import org.json.JSONObject

/**
 * PhoneticEngine V6: Corrección de alucinaciones unificada vía ULC.
 */
object PhoneticEngine {

    private const val TAG = "PhoneticEngine"
    private val phoneticCache = mutableMapOf<String, String>()

    private val dictionary: PhoneticDictionary by lazy {
        PhoneticDictionary(LogPoseApplication.instance)
    }

    // Unificación de todas las correcciones del ULC
    private val MASTER_CORRECTIONS: Map<String, String> by lazy {
        val all = mutableMapOf<String, String>()
        all.putAll(dictionary.mapaDe("alucinaciones"))
        all.putAll(dictionary.mapaDe("modismos"))
        all.putAll(dictionary.mapaDe("musica.correcciones_foneticas"))
        all
    }

    private val dynamicBias = mutableMapOf<String, String>()

    /**
     * Sincroniza el perfil personal desde el Cognitive Server del laboratorio.
     */
    fun syncWithLab() {
        val pcIp = LogPoseApplication.entryPoint.settingsManager().getString("pc_ip", "192.168.1.33") ?: "192.168.1.33"
        val url = "http://$pcIp:5000/knowledge/psp"
        
        try {
            val response = java.net.URL(url).readText()
            val json = org.json.JSONObject(response)
            val psp = json.getJSONObject("psp")
            val updates = mutableMapOf<String, String>()
            
            val keys = psp.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                updates[key] = psp.getString(key)
            }
            
            updatePersonalProfile(updates)
            Log.i(TAG, "🧠 Sincronización Exitosa: ${updates.size} reglas PSP.")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error sincronizando PSP: ${e.message}")
        }
    }

    /**
     * Inyecta conocimiento aprendido por THAMIS LAB (PSP).
     */
    fun updatePersonalProfile(updates: Map<String, String>) {
        dynamicBias.putAll(updates)
        Log.i(TAG, "🧠 PSP: Perfil actualizado con ${updates.size} nuevas correcciones.")
    }

    data class NormalizationTrace(
        val raw: String,
        val musicNormalized: String,
        val pspCorrected: String?,
        val glosarioCorrected: String?,
        val finalResult: String
    )

    private val normalizationCache = mutableMapOf<String, NormalizationTrace>()

    fun normalizeWithTrace(raw: String): NormalizationTrace {
        if (raw.isBlank()) return NormalizationTrace(raw, "", null, null, "")
        
        val cached = normalizationCache[raw]
        if (cached != null) return cached

        val tStart = System.currentTimeMillis()
        val musicNorm = MusicVocabulary.normalize(raw.lowercase().trim())
        var pspResult: String? = null
        var glosarioResult: String? = null

        // 1. Match dinámico completo (Prioridad PSP)
        dynamicBias[musicNorm]?.let { 
            pspResult = it 
            logPSPAudit("HIT", musicNorm, System.currentTimeMillis() - tStart)
        }

        // 2. Match estático completo (Glosario)
        if (pspResult == null) {
            MASTER_CORRECTIONS[musicNorm]?.let { glosarioResult = it }
            logPSPAudit(if (glosarioResult != null) "ULC_HIT" else "MISS", musicNorm, System.currentTimeMillis() - tStart)
        }

        val result = pspResult ?: glosarioResult ?: run {
            val tokens = musicNorm.split(Regex("\\s+"))
            val sb = StringBuilder()
            
            // v6.3: Búsqueda de Ventana Deslizante para sub-frases aprendidas (Misión #024)
            var i = 0
            while (i < tokens.size) {
                var foundMatch = false
                // Intentamos match de hasta 4 tokens (e.g. "alicia moreau de justo")
                for (len in 4 downTo 1) {
                    if (i + len <= tokens.size) {
                        val subPhrase = tokens.subList(i, i + len).joinToString(" ")
                        val corrected = com.uriel.logpose.thamis.learning.LearningEngine.getPhoneticCorrection(subPhrase) 
                            ?: MASTER_CORRECTIONS[subPhrase]
                        
                        if (corrected != null) {
                            if (sb.isNotEmpty()) sb.append(" ")
                            sb.append(corrected)
                            i += len
                            foundMatch = true
                            break
                        }
                    }
                }
                
                if (!foundMatch) {
                    val token = tokens[i]
                    if (token.length >= 2 || token in setOf("a", "y", "o")) {
                        if (sb.isNotEmpty()) sb.append(" ")
                        sb.append(token)
                    }
                    i++
                }
            }
            sb.toString()
        }

        val finalTrace = NormalizationTrace(
            raw = raw,
            musicNormalized = musicNorm,
            pspCorrected = pspResult,
            glosarioCorrected = glosarioResult,
            finalResult = result
        )
        
        if (normalizationCache.size > 500) normalizationCache.clear() // Prevent leak
        normalizationCache[raw] = finalTrace
        
        return finalTrace
    }

    private fun logPSPAudit(action: String, token: String, duration: Long) {
        val audit = JSONObject().apply {
            put("type", "PSP_AUDIT")
            put("action", action)
            put("token", token)
            put("duration_ms", duration)
        }
        // Enviar vía UDP al lab
        com.uriel.logpose.thamis.cognitive.CognitivePipeline.sendTelemetryProxy(audit)
    }

    fun normalize(raw: String): String = normalizeWithTrace(raw).finalResult

    /**
     * Algoritmo de Llave Fonética Rioplatense (ALF-R) v4.0 "Contextual Deep Match".
     * Soporta colapso de rimas asonantes agresivo (Fuzzy Collapse Pro) y
     * normalización de estrés vocálico para voseo adaptativo.
     */
    fun getPhoneticKey(text: String, noiseLevel: Float = 0.0f): String {
        if (text.isEmpty()) return ""
        
        // El nivel de ruido > 0.8 activa el colapso agresivo (Fuzzy Collapse Pro)
        val isExtremeNoise = noiseLevel > 0.8f
        val cacheKey = if (isExtremeNoise) "v4_noise_$text" else "v4_$text"
        
        val cached = phoneticCache[cacheKey]
        if (cached != null) return cached

        // 1. Normalización Rioplatense Dinámica (Quitar acentos de voseo y normalización Staff)
        var key = text.lowercase().trim()
            .replace("á", "a").replace("é", "e").replace("í", "i")
            .replace("ó", "o").replace("ú", "u").replace("ü", "u")

        // 2. ALF-R v4.4: Hardening Acústico contra Ruido de Motor
        // Colapso de rimas asonantes: la 'a' y la 'o' suelen sonar igual con viento fuerte
        key = key.replace("y", "i")
            .replace("ll", "i")
            .replace("sh", "i")
            .replace("ch", "i")
            .replace("v", "b")
            .replace("h", "")
            .replace("z", "s")
            .replace("c", "k")
            .replace("q", "k")
            .replace("j", "h")
            .replace("g", "h")
            .replace(Regex("s\\b"), "") // Ignorar plurales

        // 3. ALF-R v4.4: Colapso de Consonantes por Punto de Articulación (Bilabiales)
        key = key.replace("p", "b")
            .replace("t", "d")
            .replace("f", "b")
            
        // 4. Pre-procesamiento: Eliminar repeticiones por jitter de audio
        key = key.replace(Regex("([a-z])\\1+"), "$1")

        // 4. ALF-R v4.0: Fuzzy Collapse Pro (Solo bajo ruido extremo)
        if (isExtremeNoise) {
            // Colapsar consonantes por punto de articulación (Bilabiales y Dentales)
            key = key.replace("p", "b")
                .replace("t", "d")
                .replace("f", "b")
            
            // Colapsar vocales (Asonancia extrema: la 'a' y la 'o' suenan igual con viento)
            key = key.replace(Regex("[aeo]"), "V")
            key = key.replace(Regex("[iu]"), "I")
        }

        phoneticCache[cacheKey] = key
        return key
    }

    /**
     * Calcula la similitud entre dos strings basada en sus llaves fonéticas ALF-R v4.5.
     * Implementa Token-Level Matching para evitar que palabras clave se diluyan en el promedio.
     */
    fun similarity(
        s1: String, 
        s2: String, 
        noiseLevel: Float = 0.0f,
        semanticCloud: Map<String, Float> = emptyMap()
    ): Float {
        if (s1 == s2) return 1.0f
        
        val k1 = s1.split(" ").map { getPhoneticKey(it, noiseLevel) }.filter { it.isNotEmpty() }
        val k2 = s2.split(" ").map { getPhoneticKey(it, noiseLevel) }.filter { it.isNotEmpty() }
        
        if (k1.isEmpty() || k2.isEmpty()) return 0f

        // --- ALF-R v4.5: Anchor Matching Logic ---
        var matchedTokens = 0
        for (targetToken in k2) {
            // Buscamos si el token objetivo está presente en el input
            if (k1.any { it == targetToken }) {
                matchedTokens++
            } else {
                // Fallback: Similitud difusa por token si no hay match exacto
                if (k1.any { levenshtein(it, targetToken).toFloat() / maxOf(it.length, targetToken.length) < 0.3f }) {
                    matchedTokens++
                }
            }
        }
        
        var score = matchedTokens.toFloat() / k2.size.toFloat()

        // --- Semantic Boost ---
        // Aplicamos boost si el s2 (original) tiene palabras en la nube semántica
        val originalTokens = s2.lowercase().split(" ")
        for (token in originalTokens) {
            semanticCloud[token]?.let { boost ->
                score = (score + boost).coerceAtMost(1.0f)
                Log.d(TAG, "🧠 Semantic Boost (v4.5) aplicado a '$token': +$boost")
            }
        }

        return score
    }

    /**
     * Versión optimizada para el índice fonético.
     */
    fun similarityWithKeys(
        k1: String, // Input completo ya procesado
        k2: String, // Target ya procesado
        originalTarget: String,
        semanticCloud: Map<String, Float> = emptyMap()
    ): Float {
        // En esta versión, tratamos las llaves como bloques únicos para velocidad de índice
        if (k1 == k2) return 1.0f
        
        val dist = levenshtein(k1, k2)
        val maxLen = maxOf(k1.length, k2.length)
        var score = 1.0f - (dist.toFloat() / maxLen.toFloat())
        
        semanticCloud[originalTarget.lowercase()]?.let { boost ->
            score = (score + boost).coerceAtMost(1.0f)
        }
        return score
    }

    private fun levenshtein(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j
        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        return dp[s1.length][s2.length]
    }
}
