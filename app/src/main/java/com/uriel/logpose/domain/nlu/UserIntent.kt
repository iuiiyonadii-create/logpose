package com.uriel.logpose.domain.nlu

/**
 * High-level intent extracted from user speech.
 */
enum class UserIntent {
    PLAY_MUSIC,
    PAUSE_MUSIC,
    CHANGE_VOLUME,
    CALL_CONTACT,
    READ_MESSAGE,
    CHANGE_MODE,
    GET_STATUS,
    STOP_LOGPOSE,
    UNKNOWN
}
