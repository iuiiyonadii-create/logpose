package com.uriel.logpose.thamis.language

import com.uriel.logpose.thamis.normalizer.LanguageNormalizer

/**
 * Procesador principal de lenguaje para THAMIS.
 */
object LanguageProcessor {

    fun process(text: String): String {
        // 1. Normalización lingüística (tildes, caracteres especiales)
        val basic = LanguageNormalizer.normalize(text)
        
        // 2. Normalización fonética (moto-jargon, errores de Vosk)
        return PhoneticEngine.normalize(basic)
    }
}