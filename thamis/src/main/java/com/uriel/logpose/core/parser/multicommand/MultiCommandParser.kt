package com.uriel.logpose.core.parser.multicommand

object MultiCommandParser {

    fun parse(
        text: String
    ): MultiCommandResult {

        val commands =
            CommandSeparator.split(text)

        return MultiCommandResult(

            originalText = text,

            command = MultiCommand(
                commands = commands
            )
        )
    }

    fun isMultiCommand(
        text: String
    ): Boolean {

        return CommandSeparator
            .containsMultipleCommands(text)
    }
}