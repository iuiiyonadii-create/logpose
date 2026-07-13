package com.uriel.logpose.commands

import com.uriel.logpose.core.Command
import com.uriel.logpose.core.LogPoseLogger

object CommandProcessor {

    fun process(text: String): Command {

        val command = text
            .trim()
            .lowercase()

        LogPoseLogger.i("Procesando: $command")

        return when {

            command == "escuchar" ->
                Command.StartListening

            command == "detener" ->
                Command.StopListening

            command.startsWith("spotify") ->
                Command.PlayMusic("Spotify")

            command.startsWith("música") ->
                Command.PlayMusic("Spotify")

            command.startsWith("musica") ->
                Command.PlayMusic("Spotify")

            command.startsWith("llamar") -> {

                val contact = command
                    .removePrefix("llamar")
                    .trim()

                if (contact.isBlank()) {
                    Command.Unknown
                } else {
                    Command.Call(contact)
                }

            }

            command.startsWith("ir a") -> {

                val destination = command
                    .removePrefix("ir a")
                    .trim()

                if (destination.isBlank()) {
                    Command.Unknown
                } else {
                    Command.Navigate(destination)
                }

            }

            else -> Command.Unknown

        }

    }

}