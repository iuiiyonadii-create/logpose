package com.uriel.logpose.thamis_ai.nlu

/**
 * @deprecated Use com.uriel.logpose.thamis.normalizer.LanguageNormalizer instead.
 * Part of Thamis Evolution Program v5.0 - SSOT Principle.
 */
@Deprecated("Use com.uriel.logpose.thamis.normalizer.LanguageNormalizer")
class LanguageNormalizer {
    fun normalize(text: String): String = com.uriel.logpose.thamis.normalizer.LanguageNormalizer.normalize(text)
}
