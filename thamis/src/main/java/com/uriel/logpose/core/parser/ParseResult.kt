package com.uriel.logpose.core.parser

import com.thamis.lab.core.contracts.command.LogPoseCommand

sealed interface ParseResult {

    data class Success(
        val command: LogPoseCommand
    ) : ParseResult

    data object MultiSuccess : ParseResult

    data object Unknown : ParseResult

    data object Ignored : ParseResult
}