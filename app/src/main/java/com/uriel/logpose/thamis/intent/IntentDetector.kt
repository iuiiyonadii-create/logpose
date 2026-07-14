package com.uriel.logpose.thamis.intent

import com.uriel.logpose.thamis.normalizer.LanguageNormalizer

/**
 * Detecta la intención principal del usuario.
 */
object IntentDetector {

    fun detect(text: String): Intent {

        val normalized = LanguageNormalizer.normalize(text)

        for (pattern in IntentPatterns.all) {

            if (normalized in pattern.phrases) {
                return pattern.intent
            }
        }

        return Intent.UNKNOWN
    }
}