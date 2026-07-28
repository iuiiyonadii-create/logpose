package com.uriel.logpose.thamis.learning

import android.content.Context
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.intent.Intent
import org.json.JSONObject

/**
 * Motor de aprendizaje de THAMIS con persistencia.
 * Permite que el sistema aprenda de las correcciones del usuario y sus hábitos.
 */
object LearningEngine {

    private val userCorrections = mutableMapOf<String, Intent>()
    private val actionFrequency = mutableMapOf<Intent, Int>()
    
    private var isInitialized = false
    private lateinit var prefs: android.content.SharedPreferences

    fun initialize(context: Context) {
        if (isInitialized) return
        prefs = context.getSharedPreferences("thamis_learning", Context.MODE_PRIVATE)
        loadMemory()
        isInitialized = true
    }

    private fun loadMemory() {
        try {
            // Cargar correcciones
            val correctionsJson = prefs.getString("corrections", "{}") ?: "{}"
            val obj = JSONObject(correctionsJson)
            obj.keys().forEach { key ->
                val intentName = obj.getString(key)
                try {
                    userCorrections[key] = Intent.valueOf(intentName)
                } catch (e: Exception) {}
            }

            // Cargar frecuencias
            val freqJson = prefs.getString("frequencies", "{}") ?: "{}"
            val fObj = JSONObject(freqJson)
            fObj.keys().forEach { key ->
                try {
                    actionFrequency[Intent.valueOf(key)] = fObj.getInt(key)
                } catch (e: Exception) {}
            }
        } catch (e: Exception) {
            LogPoseLogger.e("🧠 Error cargando memoria de Thamis: ${e.message}")
        }
    }

    private fun saveMemory() {
        val correctionsObj = JSONObject()
        userCorrections.forEach { (text, intent) -> correctionsObj.put(text, intent.name) }
        
        val freqObj = JSONObject()
        actionFrequency.forEach { (intent, count) -> freqObj.put(intent.name, count) }

        prefs.edit()
            .putString("corrections", correctionsObj.toString())
            .putString("frequencies", freqObj.toString())
            .apply()
    }

    /**
     * Registra una corrección explícita del usuario.
     */
    fun registerCorrection(originalText: String, correctIntent: Intent) {
        val text = originalText.lowercase().trim()
        if (userCorrections[text] == correctIntent) return
        
        userCorrections[text] = correctIntent
        saveMemory()
        LogPoseLogger.i("🧠 THAMIS aprendió: '$originalText' -> $correctIntent")
        
        // Notificamos que Thamis aprendió algo (para Feedback auditivo)
        com.uriel.logpose.features.voice.FeedbackManager.speak("Entendido.")
    }

    /**
     * Devuelve una intención aprendida si existe para ese texto.
     */
    fun getLearnedIntent(text: String): Intent? {
        return userCorrections[text.lowercase().trim()]
    }

    /**
     * Registra el uso de una funcionalidad para aprender hábitos.
     */
    fun registerUsage(intent: Intent) {
        if (intent == Intent.UNKNOWN) return
        actionFrequency[intent] = (actionFrequency[intent] ?: 0) + 1
        saveMemory()
    }

    /**
     * Devuelve el peso de una intención basado en el hábito del usuario.
     */
    fun getHabitWeight(intent: Intent): Float {
        val total = actionFrequency.values.sum().toFloat()
        if (total == 0f || intent == Intent.UNKNOWN) return 1.0f
        val frequency = (actionFrequency[intent] ?: 0).toFloat()
        
        // Bonus de hasta 20% de confianza para lo más usado
        return 1.0f + (frequency / total * 0.2f)
    }

    /**
     * Olvida un patrón que ya no es válido.
     */
    fun forget(text: String) {
        userCorrections.remove(text.lowercase().trim())
        saveMemory()
    }
}
