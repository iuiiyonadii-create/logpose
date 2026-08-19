package com.uriel.logpose.core.parser.multicommand

object MultiCommandStatistics {

    private var totalRequests = 0

    private var totalCommands = 0

    fun register(
        result: MultiCommandResult
    ) {

        totalRequests++

        totalCommands +=
            result.command.size
    }

    fun totalRequests(): Int =
        totalRequests

    fun totalCommands(): Int =
        totalCommands

    fun averageCommands(): Float {

        if (totalRequests == 0) {
            return 0f
        }

        return totalCommands.toFloat() /
                totalRequests
    }

    fun reset() {

        totalRequests = 0

        totalCommands = 0
    }
}