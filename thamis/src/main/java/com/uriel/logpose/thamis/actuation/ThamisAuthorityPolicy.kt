package com.uriel.logpose.thamis.actuation

import com.thamis.lab.core.contracts.intent.Intent

/**
 * Define qué acciones tienen autorización para ser ejecutadas por THAMIS v3.0.
 */
object ThamisAuthorityPolicy {

    private val ALLOWED_INTENTS = setOf(
        Intent.PLAY_MUSIC,
        Intent.PAUSE_MUSIC,
        Intent.NEXT_TRACK,
        Intent.PREVIOUS_TRACK,
        Intent.SET_VOLUME
    )

    fun isAuthorized(intent: Intent): Boolean {
        return intent in ALLOWED_INTENTS
    }
}
