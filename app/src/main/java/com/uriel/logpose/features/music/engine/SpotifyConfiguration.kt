package com.uriel.logpose.features.music.engine

/**
 * SpotifyConfiguration v8.5: Configuración con Client ID Real.
 */
object SpotifyConfiguration {
    const val CLIENT_ID = "dfa6baa841e44a93a1da2a86f6c2736f"
    const val REDIRECT_URI = "logpose://callback"
    
    val SCOPES = arrayOf(
        "streaming",
        "user-modify-playback-state",
        "user-read-playback-state",
        "user-read-currently-playing"
    )
}
