package com.uriel.logpose.thamis.intent

/**
 * Intenciones comprendidas por THAMIS.
 */
enum class Intent {

    UNKNOWN,

    PLAY_MUSIC,
    PAUSE_MUSIC,
    NEXT_TRACK,
    PREVIOUS_TRACK,

    SET_VOLUME,

    CALL_CONTACT,
    ANSWER_CALL,
    REJECT_CALL,

    SEND_MESSAGE,

    NAVIGATE,

    WEATHER,

    EMERGENCY
}