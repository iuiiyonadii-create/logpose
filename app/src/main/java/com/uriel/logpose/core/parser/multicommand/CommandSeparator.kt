package com.uriel.logpose.core.parser.multicommand

object CommandSeparator {

    private val separators =
        SeparatorType.entries
            .sortedByDescending { it.value.length }

    fun split(
        text: String
    ): List<String> {

        var result = text

        separators.forEach { separator ->

            val regex = Regex(
                Regex.escape(separator.value),
                RegexOption.IGNORE_CASE
            )

            result = result.replace(
                regex,
                "|"
            )
        }

        return result
            .split("|")
            .map(String::trim)
            .filter(String::isNotBlank)
    }

    fun containsMultipleCommands(
        text: String
    ): Boolean {

        return separators.any { separator ->

            Regex(
                Regex.escape(separator.value),
                RegexOption.IGNORE_CASE
            ).containsMatchIn(text)
        }
    }
}