package com.uriel.logpose.compat.core

sealed class Command {

    object StartListening : Command()

    object StopListening : Command()

    data class Navigate(
        val destination: String
    ) : Command()

    data class PlayMusic(
        val app: String
    ) : Command()

    data class Call(
        val contact: String
    ) : Command()

    object Unknown : Command()

}