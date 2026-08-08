package com.uriel.logpose.core.parser.multicommand

import com.uriel.logpose.core.app.LogPoseApplication
import com.uriel.logpose.core.parser.PhoneticDictionary

object CommandSeparator {

    private val dictionary: PhoneticDictionary by lazy {
        PhoneticDictionary(LogPoseApplication.instance)
    }

    private val separators: List<String> get() = dictionary.listaDe("separadores").sortedByDescending { it.length }

    fun split(text: String): List<String> {
        var result = text
        separators.forEach { separator ->
            val regex = Regex(Regex.escape(separator), RegexOption.IGNORE_CASE)
            result = result.replace(regex, "|")
        }
        return result.split("|").map(String::trim).filter(String::isNotBlank)
    }

    fun containsMultipleCommands(text: String): Boolean {
        return separators.any { separator ->
            Regex(Regex.escape(separator), RegexOption.IGNORE_CASE).containsMatchIn(text)
        }
    }
}
