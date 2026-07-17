package com.uriel.logpose.core.parser.normalization

import java.text.Normalizer

object CommandNormalizer {

    private val stopWords = setOf(
        "por",
        "favor",
        "porfavor",
        "che",
        "eh",
        "emm",
        "mmm",
        "hola",
        "ok",
        "okay",
        "bueno",
        "entonces",
        "me",
        "podria",
        "podrias",
        "podes",
        "podés",
        "quiero",
        "quisiera",
        "el",
        "la",
        "los",
        "las",
        "un",
        "una",
        "unos",
        "unas",
        "a"
    )

    fun normalize(text: String): String {

        val cleaned = Normalizer.normalize(
            text,
            Normalizer.Form.NFD
        )
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
            .lowercase()
            .replace("[^a-z0-9\\s]".toRegex(), " ")
            .replace("\\s+".toRegex(), " ")
            .trim()

        val normalized = cleaned
            .split(" ")
            .filter {
                it.isNotBlank() &&
                        it !in stopWords
            }
            .joinToString(" ")

        return CommandAliasRepository.resolve(normalized)
    }
}