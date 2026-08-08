package com.uriel.logpose.thamis.normalizer

import java.text.Normalizer

/**
 * Normaliza el texto antes de ser procesado por THAMIS.
 */
object LanguageNormalizer {

    fun normalize(text: String): String {
        var normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
            .replace("\\p{M}+".toRegex(), "")
            .lowercase()

        // --- NORMALIZACIÓN RIOPLATENSE v1.5 ---
        // Manejo de contracciones y abreviaturas comunes en Argentina
        normalized = normalized
            .replace(Regex("\\bpal\\b"), "para el")
            .replace(Regex("\\bpala\\b"), "para la")
            .replace(Regex("\\bdsp\\b"), "despues")
            .replace(Regex("\\bdps\\b"), "despues")
            .replace(Regex("\\btmb\\b"), "tambien")
            .replace(Regex("\\bmsj\\b"), "mensaje")
            .replace(Regex("\\bwpp\\b"), "whatsapp")
            .replace(Regex("\\bwasap\\b"), "whatsapp")
            .replace(Regex("\\bubekistan\\b"), "uzbekistan")
            .replace(Regex("\\buzbe\\b"), "uzbekistan")
            .replace(Regex("\\bube\\b"), "uzbekistan")
            .replace(Regex("\\bun tan\\b"), "uzbekistan")
            .replace(Regex("\\buntan\\b"), "uzbekistan")
            .replace(Regex("\\bvegetal\\b"), "uzbekistan")

        return normalized
            .replace("[^a-z0-9 ]".toRegex(), " ")
            .replace("\\s+".toRegex(), " ")
            .trim()
    }
}