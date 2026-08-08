package com.uriel.logpose.core.services

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.audiofx.AcousticEchoCanceler
import android.media.audiofx.AutomaticGainControl
import android.media.audiofx.NoiseSuppressor
import android.os.Build
import android.os.Process
import com.uriel.logpose.core.compat.core.LogPoseLogger
import kotlinx.coroutines.*

/**
 * Capture de audio "Always-On": Mantiene el micrófono abierto durante todo el viaje
 * para evitar micro-cortes de ruteo SCO y latencia de inicialización.
 * Hardened v1.5: Blindaje multicapa de hardware y supresión de ruido avanzada.
 */
object IntercomCaptureManager {
    private const val SAMPLE_RATE = 16000
    private var audioRecord: AudioRecord? = null
    
    private var noiseSuppressor: NoiseSuppressor? = null
    private var echoCanceler: AcousticEchoCanceler? = null
    private var hardwareAgc: AutomaticGainControl? = null
    
    private var captureJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    private var restartAttempts = 0
    private const val MAX_RESTART_ATTEMPTS = 5
    private var lastCallback: ((ShortArray, Int) -> Unit)? = null
    private var isPersistent = false
    private var lastAudioChunkTimestamp = 0L

    // --- AUDIO POOL v1.7 (Misión #029) ---
    private val bufferPool = java.util.concurrent.LinkedBlockingQueue<ShortArray>(20)
    
    private fun getBufferFromPool(size: Int): ShortArray {
        return bufferPool.poll() ?: ShortArray(size)
    }

    private fun releaseBufferToPool(buffer: ShortArray) {
        bufferPool.offer(buffer)
    }

    fun isCapturing(): Boolean = audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING
    fun getActiveSessionId(): Int = audioRecord?.audioSessionId ?: -1

    @SuppressLint("MissingPermission")
    fun start(context: Context, onAudioData: (ShortArray, Int) -> Unit) {
        lastCallback = { data, len ->
            val poolBuffer = getBufferFromPool(len)
            System.arraycopy(data, 0, poolBuffer, 0, len)
            onAudioData(poolBuffer, len)
        }
        isPersistent = true
        
        if (isCapturing()) {
            LogPoseLogger.d("Capture: Micrófono ya activo. Re-usando stream.")
            return
        }
        
        LogPoseLogger.i("Capture: Iniciando micrófono persistente (Always-On).")
        internalStart(context)
    }

    /**
     * Misión #029: Permite a los consumidores devolver el buffer al pool.
     */
    fun releaseBuffer(buffer: ShortArray) {
        releaseBufferToPool(buffer)
    }

    private var currentGain = 4.0f
    private const val TARGET_RMS = 6000.0
    private const val MAX_GAIN = 15.0f
    private const val MIN_GAIN = 1.0f

    @SuppressLint("MissingPermission")
    private fun internalStart(context: Context) {
        try {
            val minBufferSize = AudioRecord.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )

            audioRecord = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                AudioRecord.Builder()
                    .setContext(context) 
                    .setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
                    .setAudioFormat(AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                        .build())
                    .setBufferSizeInBytes(minBufferSize * 2)
                    .build()
            } else {
                @Suppress("DEPRECATION")
                AudioRecord(
                    MediaRecorder.AudioSource.VOICE_COMMUNICATION,
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minBufferSize * 2
                )
            }

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                LogPoseLogger.e("Capture: Error crítico de hardware mic (STATE_UNINITIALIZED).")
                handleRestart(context)
                return
            }

            // SINCRO CLAUDE: Blindaje absoluto contra muertes de proceso por AppOps en HyperOS
            // Solo intentamos grabar si tenemos un contexto de atribución y estamos en condiciones.
            try {
                audioRecord?.startRecording()
                if (audioRecord?.recordingState != AudioRecord.RECORDSTATE_RECORDING) {
                    throw Exception("El hardware de audio no aceptó el comando startRecording.")
                }
                LogPoseLogger.i("Capture: Hardware mic activado con éxito.")
            } catch (e: Exception) {
                LogPoseLogger.e("Capture: ERROR CRÍTICO. El sistema denegó la grabación (AppOps/HyperOS): ${e.message}")
                // SINCRO CLAUDE: Si el sistema nos deniega, no intentamos re-iniciar en loop infinito
                // para evitar que el system_server nos mate el proceso por insistencia.
                audioRecord?.release()
                audioRecord = null
                isPersistent = false
                return
            }

            restartAttempts = 0 

            setupAudioEffects()
            
            launchCaptureLoop(context, minBufferSize)
            
        } catch (e: Exception) {
            LogPoseLogger.e("Capture Error: ${e.message}")
            handleRestart(context)
        }
    }

    private fun setupAudioEffects() {
        val record = audioRecord ?: return
        val sessionId = record.audioSessionId

        if (NoiseSuppressor.isAvailable()) {
            noiseSuppressor = NoiseSuppressor.create(sessionId)
            noiseSuppressor?.enabled = true
            LogPoseLogger.i("Hardware: Supresor de ruido ACTIVADO.")
        }
        
        if (AcousticEchoCanceler.isAvailable()) {
            echoCanceler = AcousticEchoCanceler.create(sessionId)
            echoCanceler?.enabled = true
            LogPoseLogger.i("Hardware: Cancelador de eco ACTIVADO.")
        }

        if (AutomaticGainControl.isAvailable()) {
            hardwareAgc = AutomaticGainControl.create(sessionId)
            hardwareAgc?.enabled = true
            LogPoseLogger.i("Hardware: AGC de hardware ACTIVADO.")
        }
    }

    private fun launchCaptureLoop(context: Context, bufferSize: Int) {
        captureJob?.cancel()
        captureJob = scope.launch {
            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO)
            val buffer = ShortArray(bufferSize)
            
            while (isPersistent) {
                val record = audioRecord ?: break
                val read = record.read(buffer, 0, buffer.size)
                
                when {
                    read == AudioRecord.ERROR_DEAD_OBJECT -> {
                        LogPoseLogger.w("Capture: ERROR_DEAD_OBJECT detectado (Watchdog gatillado).")
                        handleRestart(context)
                        return@launch
                    }
                    read < 0 -> {
                        LogPoseLogger.e("Capture: AudioRecord error $read. Reintentando...")
                        handleRestart(context)
                        return@launch
                    }
                    read > 0 -> {
                        lastAudioChunkTimestamp = System.currentTimeMillis()
                        // --- AGC DINÁMICO ---
                        processAGC(buffer, read)
                        lastCallback?.invoke(buffer, read)
                    }
                }
            }
            LogPoseLogger.w("Capture: Loop persistente finalizado.")
        }
    }

    private fun processAGC(buffer: ShortArray, read: Int) {
        var sumSq = 0.0
        for (i in 0 until read) {
            val sample = buffer[i].toDouble()
            sumSq += sample * sample
        }
        val rms = Math.sqrt(sumSq / read)
        
        // --- AGC SMART v1.6 (Optimizado para Intercoms) ---
        // Si el RMS es extremadamente bajo, el casco podría estar en standby.
        // Si es extremadamente alto, el viento está saturando.
        if (rms in 20.0..10000.0) {
            val targetGain = (TARGET_RMS / rms).toFloat().coerceIn(MIN_GAIN, MAX_GAIN)
            // Suavizado más lento para intercoms (Evita el "efecto bombeo" del ruido de viento)
            currentGain = currentGain * 0.98f + targetGain * 0.02f
        }

        for (i in 0 until read) {
            val amplified = buffer[i].toInt() * currentGain
            buffer[i] = amplified.toInt().coerceIn(-32768, 32767).toShort()
        }
    }

    fun checkHealth(context: Context) {
        if (!isPersistent) return
        
        val now = System.currentTimeMillis()
        val silenceDuration = now - lastAudioChunkTimestamp
        
        if (silenceDuration > 5000L && lastAudioChunkTimestamp > 0) {
            LogPoseLogger.w("Capture Watchdog: Silencio prolongado detectado (${silenceDuration}ms). Reiniciando hardware...")
            handleRestart(context)
        }
    }

    private fun handleRestart(context: Context) {
        if (!isPersistent) return
        
        // SINCRO CLAUDE: Limpieza rápida para recreación de emergencia
        releaseEffects()
        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (e: Exception) {
            LogPoseLogger.e("Capture: Error al detener AudioRecord durante reinicio: ${e.message}")
        }
        audioRecord = null

        if (restartAttempts < MAX_RESTART_ATTEMPTS) {
            restartAttempts++
            LogPoseLogger.w("Capture Watchdog: Re-creando AudioRecord (Intento $restartAttempts)...")
            scope.launch {
                delay(500)
                internalStart(context)
            }
        } else {
            LogPoseLogger.e("Capture: Hardware Mic falló definitivamente.")
            isPersistent = false
        }
    }

    private fun releaseEffects() {
        try {
            noiseSuppressor?.release()
            echoCanceler?.release()
            hardwareAgc?.release()
        } catch (e: Exception) {
            LogPoseLogger.w("Capture: Error liberando efectos: ${e.message}")
        }
        noiseSuppressor = null
        echoCanceler = null
        hardwareAgc = null
    }

    fun stop() {
        LogPoseLogger.i("Capture: Deteniendo micrófono persistente.")
        isPersistent = false
        captureJob?.cancel()
        releaseEffects()
        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (e: Exception) {
            LogPoseLogger.e("Capture: Error al detener AudioRecord durante reinicio: ${e.message}")
        }
        audioRecord = null
        lastCallback = null
    }
}
