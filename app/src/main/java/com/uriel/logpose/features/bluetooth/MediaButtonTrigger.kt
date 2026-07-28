package com.uriel.logpose.features.bluetooth

import android.content.Context
import android.content.Intent
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.view.KeyEvent
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.features.voice.VoiceManager

/**
 * Captura eventos de botones multimedia del intercomunicador (V6 Pro+)
 * para activar THAMIS manualmente.
 * Hardened v2.0: Soporte multi-botón y blindaje de foco de audio.
 */
class MediaButtonTrigger(private val context: Context) {

    private var mediaSession: MediaSession? = null

    fun initialize() {
        if (mediaSession != null) return

        mediaSession = MediaSession(context, "LogPoseMediaSession").apply {
            setCallback(object : MediaSession.Callback() {
                override fun onMediaButtonEvent(mediaButtonIntent: Intent): Boolean {
                    val keyEvent = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        mediaButtonIntent.getParcelableExtra(Intent.EXTRA_KEY_EVENT, KeyEvent::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        mediaButtonIntent.getParcelableExtra(Intent.EXTRA_KEY_EVENT)
                    }

                    if (keyEvent != null && keyEvent.action == KeyEvent.ACTION_DOWN) {
                        val keyCode = keyEvent.keyCode
                        LogPoseLogger.i("Intercom: Botón físico detectado (KeyCode: $keyCode)")
                        
                        when (keyCode) {
                            KeyEvent.KEYCODE_MEDIA_PLAY, 
                            KeyEvent.KEYCODE_MEDIA_PAUSE,
                            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
                            KeyEvent.KEYCODE_HEADSETHOOK,
                            KeyEvent.KEYCODE_CALL -> {
                                LogPoseLogger.i("Intercom: Gatillo de activación reconocido.")
                                VoiceManager.start()
                                return true
                            }
                            KeyEvent.KEYCODE_MEDIA_NEXT -> {
                                LogPoseLogger.d("Intercom: Siguiente track solicitado físicamente.")
                            }
                            KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
                                LogPoseLogger.d("Intercom: Anterior track solicitado físicamente.")
                            }
                        }
                    }
                    return super.onMediaButtonEvent(mediaButtonIntent)
                }
            })

            // Acciones soportadas para interceptar el foco de botones del sistema
            val playbackState = PlaybackState.Builder()
                .setActions(
                    PlaybackState.ACTION_PLAY or 
                    PlaybackState.ACTION_PAUSE or 
                    PlaybackState.ACTION_PLAY_PAUSE or
                    PlaybackState.ACTION_SKIP_TO_NEXT or
                    PlaybackState.ACTION_SKIP_TO_PREVIOUS
                )
                .setState(PlaybackState.STATE_PLAYING, 0, 1.0f)
                .build()

            setPlaybackState(playbackState)
            isActive = true
        }
        
        LogPoseLogger.i("MediaButtonTrigger: Blindaje de botones activado.")
    }

    fun destroy() {
        mediaSession?.isActive = false
        mediaSession?.release()
        mediaSession = null
    }
}
