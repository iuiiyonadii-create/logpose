package com.uriel.logpose.core.services

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.telecom.Connection
import android.telecom.DisconnectCause
import android.os.Build
import com.uriel.logpose.core.compat.core.LogPoseLogger

class LogPoseConnection(private val context: Context) : Connection() {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private lateinit var routingWatchdog: BluetoothRoutingWatchdog

    init {
        connectionProperties = PROPERTY_SELF_MANAGED
        // Quitamos CAPABILITY_HOLD para que el V6 no crea que puede "poner en espera" la app
        connectionCapabilities = CAPABILITY_MUTE or CAPABILITY_SUPPORT_HOLD
        audioModeIsVoip = true
        
        setupWatchdog()
    }

    private fun setupWatchdog() {
        routingWatchdog = BluetoothRoutingWatchdog(
            audioManager = audioManager,
            tag = "Telecom-SCO",
            onRouted = { device ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    audioManager.setCommunicationDevice(device)
                    LogPoseLogger.i("Telecom: Hardware LOCK en ${device.productName}")
                }
            },
            onFallback = {
                LogPoseLogger.w("Telecom: Usando altavoz por falta de respuesta BT.")
            }
        )
    }

    override fun onStateChanged(state: Int) {
        super.onStateChanged(state)
        when (state) {
            STATE_ACTIVE -> {
                LogPoseLogger.i("Telecom: Sesión ACTIVADA.")
                requestSmartFocusAndStartWatchdog()
            }
            STATE_HOLDING -> {
                LogPoseLogger.w("Telecom: Re-activando desde HOLD.")
                setActive()
            }
        }
    }

    private fun requestSmartFocusAndStartWatchdog() {
        // Claude PRO TIP: Foco normal para convivir, no exclusivo
        val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build())
            .setWillPauseWhenDucked(false)
            .setOnAudioFocusChangeListener { focusChange ->
                when (focusChange) {
                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> ComfortNoiseManager.duck()
                    AudioManager.AUDIOFOCUS_GAIN -> ComfortNoiseManager.restoreVolume()
                }
            }
            .build()

        audioManager.requestAudioFocus(focusRequest)
        audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
        
        // El ruteo de hardware es inmediato; si en 4s no hubo evento, fallback a altavoz.
        routingWatchdog.start(4000L)
    }

    override fun onDisconnect() {
        LogPoseLogger.i("Telecom: Sesión FINALIZADA.")
        routingWatchdog.stop()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            audioManager.clearCommunicationDevice()
        }
        audioManager.mode = AudioManager.MODE_NORMAL
        setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
        destroy()
    }
}
