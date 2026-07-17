package com.uriel.logpose.core.parser

import com.uriel.logpose.core.compat.core.Command

sealed interface ParseResult {

    data class Success(
        val command: Command
    ) : ParseResult

    data object Unknown : ParseResult
}