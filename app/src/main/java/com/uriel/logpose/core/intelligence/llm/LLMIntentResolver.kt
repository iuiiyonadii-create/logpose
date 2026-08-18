package com.uriel.logpose.core.intelligence.llm

import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.core.intelligence.ThamisNeuralEngine
import org.json.JSONObject

/**
 * LLMIntentResolver v67.0: Procesador de tokens ultra-eficiente.
 * v67.0: Uso del ThamisNeuralEngine unificado.
 */
class LLMIntentResolver {

    suspend fun resolveIntent(text: String): Resolution? {
        val context = com.uriel.logpose.core.intelligence.memory.VectorMemoryEngine.retrieveContext(text)
        
        // v66.0: Prompt Ultra-Compacto (Ahorro de 60% de tokens)
        // v68.5: Hardened Trip Termination instructions
        val prompt = """
            Context: $context
            Task: Resolve intent for motor rider.
            Rules: Use memory facts. If music, return CLEAN title.
            Rules: If 'viaje' or 'recorrido' + 'cancel'/'stop' -> Intent: STOP_NAVIGATION.
            Input: "$text"
            JSON: {"intent":"PLAY_MUSIC","entity":"CLEAN_NAME","confidence":0.99}
        """.trimIndent()

        val response = ThamisNeuralEngine.generateResponse(prompt)

        return try {
            val json = JSONObject(response.substringAfter("{").substringBeforeLast("}") + "}")
            val intentStr = json.getString("intent")
            val intent = try { Intent.valueOf(intentStr) } catch (_: Exception) { Intent.UNKNOWN }
            
            Resolution(
                intent = intent,
                entities = mapOf("media" to json.optString("entity"), "parameter" to json.optString("entity")),
                confidence = json.optDouble("confidence", 0.0).toFloat()
            )
        } catch (_: Exception) { null }
    }

    data class Resolution(
        val intent: Intent,
        val entities: Map<String, String>,
        val confidence: Float
    )
}
