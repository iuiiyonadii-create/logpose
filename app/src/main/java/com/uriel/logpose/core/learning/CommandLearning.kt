package com.uriel.logpose.core.learning

import com.uriel.logpose.core.compat.core.Command
import com.uriel.logpose.core.memory.CommandMemory

object CommandLearning {

    fun patterns(): List<CommandPattern> {

        return CommandMemory
            .recent(100)
            .groupBy {
                it.command
            }
            .map { entry ->

                CommandPattern(
                    command = entry.key,
                    executions = entry.value.size,
                    lastUsed = entry.value.maxOf {
                        it.timestamp
                    }
                )
            }
    }

    fun mostUsed(): CommandPattern? {

        return patterns()
            .maxByOrNull {
                it.executions
            }
    }
}