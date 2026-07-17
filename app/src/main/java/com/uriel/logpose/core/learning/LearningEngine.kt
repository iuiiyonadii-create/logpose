package com.uriel.logpose.core.learning

object LearningEngine {

    fun registerSuccess(
        command: String
    ) {

        CommandMemory.add(

            CommandSession(

                command = command,

                timestamp = System.currentTimeMillis(),

                success = true
            )
        )
    }

    fun registerFailure(
        command: String
    ) {

        CommandMemory.add(

            CommandSession(

                command = command,

                timestamp = System.currentTimeMillis(),

                success = false
            )
        )
    }

    fun suggestions(): List<CommandSuggestion> {

        return CommandMemory
            .history()
            .groupBy {
                it.command
            }
            .map {

                CommandSuggestion(

                    command = it.key,

                    uses = it.value.size
                )
            }
            .sortedByDescending {
                it.uses
            }
    }
}