package com.uriel.logpose.thamis.learning.mismatch

/**
 * Clasificación de errores entre lo que el usuario espera y lo que Vosk entrega.
 */
enum class MismatchType {
    PHONETIC_ERROR,  // "Duki" -> "Duque"
    ENTITY_ERROR,    // "Rockstar" -> "Rock Star App"
    INTENT_ERROR,    // "Poné música" -> "Abre música"
    NOISE_ERROR      // "Subilo" -> "Subido" (por viento)
}
