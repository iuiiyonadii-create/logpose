package com.uriel.logpose.thamis.intent

import com.uriel.logpose.thamis.knowledge.KnowledgeBase
import com.uriel.logpose.thamis.language.LanguageProcessor
import com.uriel.logpose.thamis.language.PhoneticEngine
import com.uriel.logpose.thamis.language.SimilarityEngine
import com.uriel.logpose.thamis.learning.LearningEngine
import com.uriel.logpose.thamis.context.THAMISContext

/**
 * Detecta la intención principal del usuario utilizando similitud lingüística, fonética y aprendizaje.
 * Optimizada para entornos con viento y ruido de motor.
 */
object IntentDetector {

    private const val MIN_SIMILARITY_THRESHOLD = 0.45f

    fun detect(text: String): DetectionResult {
        val processedText = LanguageProcessor.process(text)
        
        // 0. Prioridad Apertura de Apps (Para evitar que se confunda con música)
        if (processedText.startsWith("abrir") || processedText.startsWith("abri") || 
            processedText.startsWith("abrí") || processedText.contains("tiffany")) {
            return DetectionResult(Intent.OPEN_APP, 1.0f)
        }

        // 0.0.1 Prioridad Navegación Directa (Comandos de una palabra o muy cortos)
        if (processedText == "ir" || processedText == "como vamos" || processedText == "cómo vamos") {
            return DetectionResult(Intent.NAVIGATE, 1.0f)
        }

        // 0.1 Aprendizaje
        val learnedIntent = LearningEngine.getLearnedIntent(processedText)
        if (learnedIntent != null) {
            return DetectionResult(learnedIntent, 1.0f)
        }

        // 0.2 Contexto de Mensajería: Si acabamos de abrir WhatsApp, priorizamos SEND_MESSAGE
        if (THAMISContext.getActiveIntent() == Intent.OPEN_APP && processedText.contains("whatsapp")) {
             // Si el usuario dice algo justo después de abrir WhatsApp, es probable que sea el destinatario
             return DetectionResult(Intent.SEND_MESSAGE, 0.85f)
        }
        
        var bestIntent = Intent.UNKNOWN
        var maxScore = 0f

        val activeContext = THAMISContext.getActiveIntent()

        // 1. Buscamos coincidencias de palabras clave de acción
        val words = processedText.split(" ")
        val firstWord = words.firstOrNull() ?: ""
        
        for (rule in KnowledgeBase.entries) {
            // BOOST CONTEXTUAL: Si la regla coincide con el contexto activo, subimos el peso
            val contextBoost = if (rule.intent == activeContext) 1.2f else 1.0f
            
            for (phrase in rule.phrases) {
                val commandVerb = phrase.split(" ").firstOrNull() ?: ""
                
                // Comparación fonética del verbo (el viento suele deformar el final de la palabra)
                val phoneticScore = PhoneticEngine.similarity(firstWord, commandVerb)
                
                // Optimizamos para que 'ir' (longitud 2) funcione como comando directo si matchea exacto o casi exacto
                val minLen = if (commandVerb == "ir") 1 else 2
                
                if (commandVerb.length > minLen && (firstWord == commandVerb || (phoneticScore * contextBoost) > 0.85f)) {
                    val habitWeight = LearningEngine.getHabitWeight(rule.intent)
                    return DetectionResult(rule.intent, 0.9f * habitWeight * contextBoost)
                }
            }
        }

        // 2. Similitud global (Jaccard + Phonetic fallback)
        for (rule in KnowledgeBase.entries) {
            val contextBoost = if (rule.intent == activeContext) 1.15f else 1.0f
            
            for (phrase in rule.phrases) {
                // Similitud de palabras
                val jaccardScore = SimilarityEngine.score(processedText, phrase)
                // Similitud de sonido (Levenshtein)
                val phoneticScore = PhoneticEngine.similarity(processedText, phrase)
                
                val combinedScore = ((jaccardScore * 0.4f) + (phoneticScore * 0.6f)) * contextBoost
                
                if (combinedScore > maxScore) {
                    maxScore = combinedScore
                    bestIntent = rule.intent
                }
                
                if (maxScore >= 1.0f) break
            }
        }

        val habitBoost = LearningEngine.getHabitWeight(bestIntent)
        val finalScore = (maxScore * habitBoost).coerceAtMost(1.0f)

        val finalIntent = if (finalScore >= MIN_SIMILARITY_THRESHOLD) bestIntent else Intent.UNKNOWN
        return DetectionResult(finalIntent, finalScore)
    }
}
