package com.uriel.logpose.thamis.intent

import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.core.app.LogPoseApplication
import com.uriel.logpose.core.parser.PhoneticDictionary
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * ComplexIntentDetector: Soporte para comandos multi-paso (Misión #016).
 * Ejemplo: "Poné rock y después llamá a casa".
 */
object ComplexIntentDetector {

    private val dictionary: PhoneticDictionary by lazy {
        PhoneticDictionary(LogPoseApplication.instance)
    }

    /**
     * Procesa una cadena de texto y extrae una lista de intenciones secuenciales.
     */
    fun detectSequence(text: String): List<DetectionResult> {
        val separators = dictionary.listaDe("separadores")
        
        // 1. Intentar dividir por los separadores más complejos primero
        var parts = listOf(text)
        for (sep in separators.sortedByDescending { it.length }) {
            val newParts = mutableListOf<String>()
            for (p in parts) {
                if (p.contains(sep, ignoreCase = true)) {
                    newParts.addAll(p.split(Regex("(?i)$sep")).filter { it.isNotBlank() })
                } else {
                    newParts.add(p)
                }
            }
            parts = newParts
        }

        LogPoseLogger.d("ComplexIntentDetector: Dividido en ${parts.size} partes: $parts")

        // 2. Detectar cada parte individualmente
        val results = parts.map { part ->
            IntentDetector.detect(part.trim())
        }

        // 3. Filtrar desconocidos si hay al menos uno válido
        val validResults = results.filter { it.intent != Intent.UNKNOWN }
        
        return if (validResults.isEmpty() && results.isNotEmpty()) {
            listOf(results.first()) // Devolver el primero aunque sea UNKNOWN
        } else {
            validResults
        }
    }
}
