package com.uriel.logpose.core.learning

object CommandStatistics {

    fun totalCommands(): Int =
        CommandMemory.size()

    fun successfulCommands(): Int =
        CommandMemory.history()
            .count {
                it.success
            }

    fun failedCommands(): Int =
        CommandMemory.history()
            .count {
                !it.success
            }

    fun successRate(): Float {

        val total = totalCommands()

        if (total == 0) {
            return 0f
        }

        return successfulCommands()
            .toFloat() / total
    }
}