package com.uriel.logpose.features.music

/**
 * Estados internos del módulo de música según especificación del Sector 3.
 */
enum class MusicState {
    IDLE,
    MUSIC_PLAYING,
    MUSIC_PAUSED,
    MUSIC_STOPPED,
    MUSIC_UNAVAILABLE,
    LOADING,
    DEFAULT_PLAYER_SELECTED,
    PLAYER_SELECTION_REQUIRED
}
