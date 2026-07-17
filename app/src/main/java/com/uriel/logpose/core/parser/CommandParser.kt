package com.uriel.logpose.core.parser

import com.uriel.logpose.core.compat.core.Command

object CommandParser {

    fun parse(text: String): ParseResult {

        val value = text.trim().lowercase()

        return when {

            value == "escuchar" ->
                ParseResult.Success(
                    Command.StartListening
                )

            value == "detener" ->
                ParseResult.Success(
                    Command.StopListening
                )

            value.startsWith("llamar ") ->
                parseCall(value)

            value.startsWith("ir ") ->
                parseNavigation(value)

            value.startsWith("abrir musica ") ->
                parseMusic(value)

            value.startsWith("abrir spotify") ->
                ParseResult.Success(
                    Command.PlayMusic("spotify")
                )

            value == "musica spotify" ->
                ParseResult.Success(
                    Command.PlayMusic("spotify")
                )

            else ->
                ParseResult.Unknown
        }
    }

    private fun parseCall(
        value: String
    ): ParseResult {

        val contact = value
            .removePrefix("llamar")
            .trim()

        if (contact.isBlank()) {
            return ParseResult.Unknown
        }

        return ParseResult.Success(
            Command.Call(contact)
        )
    }

    private fun parseNavigation(
        value: String
    ): ParseResult {

        val destination = value
            .removePrefix("ir")
            .removePrefix("a")
            .trim()

        if (destination.isBlank()) {
            return ParseResult.Unknown
        }

        return ParseResult.Success(
            Command.Navigate(destination)
        )
    }

    private fun parseMusic(
        value: String
    ): ParseResult {

        val app = value
            .removePrefix("abrir musica")
            .trim()

        if (app.isBlank()) {
            return ParseResult.Unknown
        }

        return ParseResult.Success(
            Command.PlayMusic(app)
        )
    }
}