package com.uriel.logpose.core.services

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Silent Pilot: Mantiene el canal SCO abierto con consumo mínimo de energía.
 * Optimizado para MODE_STATIC para permitir el sleep de la CPU.
 */
object ComfortNoiseManager {
    private var audioTrack: AudioTrack? = null
    private var isRunning = false

    fun start(context: Context, isScoActive: Boolean) {
        if (isRunning) return
        
        if (!isScoActive) {
            LogPoseLogger.d("Hardware: Silent Pilot saltado (SCO no activo).")
            return
        }

        LogPoseLogger.d("Hardware: Iniciando Silent Pilot (Modo Estático).")
        
        try {
            val sampleRate = 8000
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate, 
                AudioFormat.CHANNEL_OUT_MONO, 
                AudioFormat.ENCODING_PCM_16BIT
            )

            // Buffer de 1 segundo de silencio digital (Ceros absolutos)
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

            // Cargamos el silencio y configuramos el loop infinito
            audioTrack?.apply {
                write(silenceBuffer, 0, silenceBuffer.size)
                setLoopPoints(0, silenceBuffer.size, -1) 
                setVolume(0.0f) // Iniciamos en silencio total por hardware
                play()
            }

            isRunning = true
            
        } catch (e: Exception) {
            LogPoseLogger.e("ComfortNoise Error: ${e.message}")
        }
    }

    fun duck() {
        try {
            audioTrack?.setVolume(0.0f)
        } catch (e: Exception) {
            LogPoseLogger.e("ComfortNoise: Error al bajar volumen: ${e.message}")
        }
    }

    fun restoreVolume() {
        try {
            audioTrack?.setVolume(1.0f)
        } catch (e: Exception) {
            LogPoseLogger.e("ComfortNoise: Error al restaurar volumen: ${e.message}")
        }
    }

    fun getTrack(): AudioTrack? = audioTrack

    fun stop() {
        LogPoseLogger.d("Hardware: Deteniendo Silent Pilot.")
        isRunning = false
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            LogPoseLogger.e("ComfortNoise: Error al detener: ${e.message}")
        }
        audioTrack = null
    }
}
