package com.uriel.logpose.thamis.normalizer

import java.text.Normalizer

/**
 * Normaliza el texto antes de ser procesado por THAMIS.
 */
object LanguageNormalizer {

    fun normalize(text: String): String {

        return Normalizer.normalize(text, Normalizer.Form.NFD)
            .replace("\\p{M}+".toRegex(), "")
            .lowercase()
            .replace("[^a-z0-9 ]".toRegex(), " ")
            .replace("\\s+".toRegex(), " ")
            .trim()
    }
}