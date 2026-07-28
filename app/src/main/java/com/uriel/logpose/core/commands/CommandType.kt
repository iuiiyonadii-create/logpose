package com.uriel.logpose.core.commands

/**
 * Enumeration of all possible physical commands recognized by the system.
 */
enum class CommandType {
    PLAY_MUSIC,
    PAUSE_MUSIC,
    NEXT_TRACK,
    PREVIOUS_TRACK,
    VOLUME_UP,
    VOLUME_DOWN,
    MUTE,
    CALL,
    ANSWER_CALL,
    REJECT_CALL,
    DRIVING_MODE,
    GET_STATUS,
    EXIT_PRIVACY,
    UNKNOWN
}
