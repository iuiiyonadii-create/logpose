package com.uriel.logpose.core.analytics

import com.uriel.logpose.core.context.CommandHistory

object CommandAnalytics {

    fun getStats(): CommandStats {

        val history = CommandHistory.all()

        return CommandStats(
            totalCommands = history.size,
            successfulCommands = history.count {
                it.success
            },
            failedCommands = history.count {
                !it.success
            }
        )
    }

    fun getLastCommand() =
        CommandHistory.last()
}