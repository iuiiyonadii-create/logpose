package com.uriel.logpose.domain.models

/**
 * Domain model representing valid voice commands for the LogPose MVP.
 */
enum class LogPoseCommand {
    PLAY,
    PAUSE,
    NEXT,
    PREVIOUS,
    VOLUME_UP,
    VOLUME_DOWN,
    START_LOGPOSE,
    STOP_LOGPOSE,
    PRIVACY_MODE,
    EXIT_PRIVACY,
    UNKNOWN
}
