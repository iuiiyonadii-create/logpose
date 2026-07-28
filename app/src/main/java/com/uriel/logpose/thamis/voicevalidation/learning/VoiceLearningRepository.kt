package com.uriel.logpose.thamis.voicevalidation.learning

import com.uriel.logpose.thamis.voicevalidation.model.RecognitionEvaluation

/**
 * Repositorio de aprendizaje para registrar errores y mejoras necesarias.
 */
object VoiceLearningRepository {
    
    private val problematicPhrases = mutableListOf<String>()
    private val errorHistory = mutableListOf<RecognitionEvaluation>()

    fun recordError(evaluation: RecognitionEvaluation) {
        errorHistory.add(evaluation)
        if (!problematicPhrases.contains(evaluation.inputPhrase)) {
            problematicPhrases.add(evaluation.inputPhrase)
        }
    }

    fun getMostProblematic(): List<String> {
        return problematicPhrases.toList()
    }

    fun clear() {
        problematicPhrases.clear()
        errorHistory.clear()
    }
}
