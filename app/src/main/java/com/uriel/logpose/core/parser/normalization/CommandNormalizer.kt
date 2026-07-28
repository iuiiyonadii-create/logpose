package com.uriel.logpose.core.parser.normalization

import java.text.Normalizer

object CommandNormalizer {

    private val stopWords = setOf(
        "logpose",
        "log",
        "pose",
        "lo",
        "love",
        "che",
        "hey",
        "hola",
        "por",
        "favor",
        "podes",
        "podés",
        "quiero",
        "quisiera",
        "me",
        "el",
        "la",
        "los",
        "las",
        "un",
        "una",
        "a",
        "al"
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