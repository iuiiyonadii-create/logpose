package com.uriel.logpose.core.parser.multicommand

object CommandSeparator {

    private val separators = SeparatorType.entries

    fun split(

        text: String

    ): List<String> {

        var result = text

        separators.forEach {

            result = result.replace(
                it.value,
                "|"
            )
        }

        return result
            .split("|")
            .map {
                it.trim()
            }
            .filter {
                it.isNotBlank()
            }
    }

    fun containsMultipleCommands(

        text: String

    ): Boolean {

        return separators.any {

            text.contains(
                it.value,
                ignoreCase = true
            )
        }
    }
}