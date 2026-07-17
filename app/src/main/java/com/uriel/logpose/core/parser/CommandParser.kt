package com.uriel.logpose.core.parser

import com.uriel.logpose.core.compat.core.Command

object CommandParser {

    fun parse(text: String): ParseResult {

        val value = text.trim().lowercase()

        return when {

            value == "escuchar" ->
                ParseResult.Success(Command.StartListening)

            value == "detener" ->
                ParseResult.Success(Command.StopListening)

            value.startsWith("llamar ") ->
                ParseResult.Success(
                    Command.Call(
                        value.removePrefix("llamar ").trim()
                    )
                )

            value.startsWith("ir a ") ->
                ParseResult.Success(
                    Command.Navigate(
                        value.removePrefix("ir a ").trim()
                    )
                )

            value.startsWith("abrir musica ") ->
                ParseResult.Success(
                    Command.PlayMusic(
                        value.removePrefix("abrir musica ").trim()
                    )
                )

            else ->
                ParseResult.Unknown
        }
    }
}