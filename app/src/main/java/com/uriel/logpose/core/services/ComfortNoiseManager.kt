package com.uriel.logpose.core.services

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.uriel.logpose.core.compat.core.LogPoseLogger
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Silent Pilot v2.0: Arquitectura DI (Misión #115).
 */
@Singleton
class ComfortNoiseManager @Inject constructor() {

    companion object {
        private var instance: ComfortNoiseManager? = null

        fun duck() {
            instance?.duck()
        }

        fun restoreVolume() {
            instance?.restoreVolume()
        }
    }

    init {
        instance = this
    }
    private var audioTrack: AudioTrack? = null
    private var isRunning = false

    fun start(context: Context, isScoActive: Boolean) {
        if (isRunning) return
        if (!isScoActive) return
        
        try {
            val sampleRate = 8000
            val silenceBuffer = ShortArray(sampleRate) { 0 } 

            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build())
                .setAudioFormat(AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build())
                .setBufferSizeInBytes(silenceBuffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack?.apply {
                write(silenceBuffer, 0, silenceBuffer.size)
                setLoopPoints(0, silenceBuffer.size, -1) 
                setVolume(0.0f)
                play()
            }
            isRunning = true
        } catch (e: Exception) {
            LogPoseLogger.e("ComfortNoise Error: ${e.message}")
        }
    }

    fun duck() {
        try { audioTrack?.setVolume(0.0f) } catch (e: Exception) {}
    }

    fun restoreVolume() {
        try { audioTrack?.setVolume(1.0f) } catch (e: Exception) {}
    }

    fun stop() {
        isRunning = false
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {}
        audioTrack = null
    }
}
