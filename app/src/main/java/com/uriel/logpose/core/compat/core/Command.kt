package com.uriel.logpose.core.compat.core

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

    sealed class System : Command() {
        object GetWeather : System()
    }

    object Unknown : Command()

}

/** Alias para compatibilidad de nomenclatura LogPoseCommand.System.* */
typealias LogPoseCommand = Command