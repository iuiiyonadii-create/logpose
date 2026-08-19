package com.uriel.logpose.features.music.model

/**
 * Representa los estados de la conexión con el controlador de Spotify.
 */
enum class SpotifyConnectionState {
    CONNECTED,
    CONNECTING,
    DISCONNECTED,
    UNAVAILABLE
}
