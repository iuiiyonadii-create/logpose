package com.uriel.logpose.thamis.voiceexperience.response

import com.uriel.logpose.thamis.voiceexperience.model.ResponseStyle
import com.uriel.logpose.thamis.voiceexperience.model.DrivingContext

/**
 * Define el estilo de la respuesta según la situación del conductor.
 */
object ResponseStyleEngine {

    fun determineStyle(context: DrivingContext): ResponseStyle {
        return when {
            context.speedKmh > 120f -> ResponseStyle.EMERGENCY
            context.speedKmh > 80f -> ResponseStyle.SHORT
            context.isCallActive -> ResponseStyle.SHORT
            else -> ResponseStyle.NORMAL
        }
    }

    fun formatMessage(message: String, style: ResponseStyle): String {
        return when (style) {
            ResponseStyle.SHORT -> message.substringBefore(".")
            ResponseStyle.EMERGENCY -> "Atención. $message"
            else -> message
        }
    }
}
