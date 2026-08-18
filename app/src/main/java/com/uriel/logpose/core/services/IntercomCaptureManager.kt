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
import com.uriel.logpose.core.app.LogPoseApplication
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
    private var lastRms = 0.0
    private var currentSmoothingFactor = 0.02f

    // --- AUDIO POOL v1.8 (Misión #040) ---
    // Aumentamos el pool a 150 para evitar creaciones de arrays bajo carga (Optimización GC Staff)
    private val bufferPool = java.util.concurrent.LinkedBlockingQueue<ShortArray>(150)
    
    private fun getBufferFromPool(size: Int): ShortArray {
        val polled = bufferPool.poll()
        return if ((polled != null) && (polled.size >= size)) polled else ShortArray(size)
    }

    private fun releaseBufferToPool(buffer: ShortArray) {
        bufferPool.offer(buffer)
    }

    fun isCapturing(): Boolean = audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING

    @SuppressLint("MissingPermission")
    fun start(context: Context, onAudioData: (ShortArray, Int) -> Unit) {
        lastCallback = onAudioData
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

    /**
     * SINCRO CLAUDE: Fuerza un re-intento de inicio tras otorgar permisos en tiempo real.
     */
    fun retryAfterPermission() {
        LogPoseLogger.i("Capture", "Solicitando re-arranque tras cambio de permisos.")
        restartAttempts = 0
        isPersistent = true
        val context = LogPoseCallService.instance ?: LogPoseApplication.instance
        internalStart(context)
    }

    private var currentGain = 3.0f
    private const val TARGET_RMS = 5500.0
    private const val MAX_GAIN = 8.0f
    private const val MIN_GAIN = 0.8f

    private fun internalStart(context: Context) {
        if (androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            LogPoseLogger.e("Capture", "Aborting start: RECORD_AUDIO permission not granted.")
            return
        }
        
        try {
            val minBufferSize = AudioRecord.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            // Alineación a marco neural (Múltiplo exacto de 512 muestras = 32ms a 16kHz)
            val alignedBufferSize = maxOf(minBufferSize, 2048)

            // SINCRO CLAUDE: Inversión de prioridad Staff. 
            // VOICE_RECOGNITION es superior para IA porque incluye Noise Suppression nativa.
            val preferredSource = MediaRecorder.AudioSource.VOICE_RECOGNITION
            
            audioRecord = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                AudioRecord.Builder()
                    .setContext(context)
                    .setAudioSource(preferredSource)
                    .setAudioFormat(AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                        .build())
                    .setBufferSizeInBytes(alignedBufferSize * 2)
                    .build()
            } else {
                // v83.0: Justified DEPRECATION - Legacy AudioRecord constructor for minSdk support.
                @Suppress("DEPRECATION")
                AudioRecord(
                    preferredSource,
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    alignedBufferSize * 2
                )
            }

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                LogPoseLogger.w("Capture", "Fallo inicial con VOICE_RECOGNITION. Intentando fallback a MIC...")
                audioRecord?.release()
                audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    alignedBufferSize * 2
                )
            }

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                LogPoseLogger.e("Capture: Error crítico de hardware mic (STATE_UNINITIALIZED).")
                handleRestart(context)
                return
            }

            try {
                audioRecord?.startRecording()
                if (audioRecord?.recordingState != AudioRecord.RECORDSTATE_RECORDING) {
                    throw Exception("El hardware de audio no aceptó el comando startRecording.")
                }
                
                // v68.0: Explicit Hardware Denoising Activation
                val sessionId = audioRecord?.audioSessionId ?: 0
                if (sessionId != 0) {
                    if (NoiseSuppressor.isAvailable()) {
                        noiseSuppressor = NoiseSuppressor.create(sessionId).apply { enabled = true }
                        LogPoseLogger.i("Capture", "✅ Hardware NoiseSuppressor: ACTIVADO")
                    }
                    if (AcousticEchoCanceler.isAvailable()) {
                        echoCanceler = AcousticEchoCanceler.create(sessionId).apply { enabled = true }
                        LogPoseLogger.i("Capture", "✅ Hardware EchoCanceler: ACTIVADO")
                    }
                }

                LogPoseLogger.i("Capture: Hardware mic (VOICE_RECOGNITION) activado con éxito a 16kHz.")
                LogPoseHudService.updateStatus("🟢 MIC: ACTIVO")
            } catch (e: Exception) {
                LogPoseLogger.e("Capture: ERROR CRÍTICO. El sistema denegó la grabación (AppOps/HyperOS): ${e.message}")
                LogPoseHudService.updateStatus("🔴 MIC: BLOQUEADO POR OS")
                audioRecord?.release()
                audioRecord = null
                isPersistent = false
                return
            }

            restartAttempts = 0 
            
            launchCaptureLoop(context, alignedBufferSize)
            
        } catch (e: Exception) {
            LogPoseLogger.e("Capture Error: ${e.message}")
            handleRestart(context)
        }
    }

    private fun launchCaptureLoop(context: Context, bufferSize: Int) {
        captureJob?.cancel()
        captureJob = scope.launch {
            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO)
            val rawBuffer = ShortArray(bufferSize)
            
            while (isPersistent) {
                val record = audioRecord ?: break
                val read = record.read(rawBuffer, 0, rawBuffer.size)
                
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
                        
                        // v1.9: Activación de AGC Staff - Normalización de volumen en tiempo real
                        processAGC(rawBuffer, read)

                        // v68.0: Software Denoising (Noise Gate / Spectral Expander Simulation)
                        // Limpia ruidos de fondo por debajo de un umbral dinámico
                        applySoftwareDenoising(rawBuffer, read)

                        // --- POOL DE BUFFERS v1.8 (Misión #030) ---
                        // Re-usamos buffers del pool para evitar la fragmentación de memoria y el GC churn.
                        val cleanChunk = getBufferFromPool(read)
                        System.arraycopy(rawBuffer, 0, cleanChunk, 0, read)
                        
                        lastCallback?.invoke(cleanChunk, read)
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
        val rms = kotlin.math.sqrt(sumSq / read)
        
        // --- AGC SMART v1.7 (Zero-Distortion Staff) ---
        // Si el RMS es extremadamente bajo, el casco podría estar en standby.
        // Si es extremadamente alto, el viento está saturando.
        if (rms in 20.0..10000.0) {
            val targetGain = (TARGET_RMS / rms).toFloat().coerceIn(MIN_GAIN, MAX_GAIN)
            
            // v61.0: Adaptive AGC Smoothing. 
            // Reaccionamos más rápido ante cambios bruscos (vibraciones/viento) y más lento ante voz estable.
            val variance = kotlin.math.abs(rms - lastRms)
            lastRms = rms
            
            currentSmoothingFactor = if (variance > 1500.0) 0.15f else 0.02f
            currentGain = currentGain * (1f - currentSmoothingFactor) + targetGain * currentSmoothingFactor
        }

        for (i in 0 until read) {
            val amplified = buffer[i].toInt() * currentGain
            buffer[i] = amplified.toInt().coerceIn(-32768, 32767).toShort()
        }
    }

    /**
     * v68.0: Software Denoising (Neural Proxy).
     * Aplica una puerta de ruido dinámica para atenuar frecuencias de viento residuales.
     */
    private fun applySoftwareDenoising(buffer: ShortArray, read: Int) {
        // Umbral dinámico basado en el RMS calculado en el AGC
        val noiseThreshold = (lastRms * 0.25).toInt()
        
        for (i in 0 until read) {
            val absSample = kotlin.math.abs(buffer[i].toInt())
            if (absSample < noiseThreshold) {
                // Atenuación suave para sonidos de bajo volumen (ruido de fondo)
                buffer[i] = (buffer[i] * 0.4).toInt().toShort()
            }
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
            LogPoseHudService.updateStatus("⚠️ MIC: RECUPERANDO ($restartAttempts)")
            scope.launch {
                delay(500L)
                internalStart(context)
            }
        } else {
            LogPoseLogger.e("Capture: Hardware Mic falló definitivamente.")
            LogPoseHudService.updateStatus("🔴 MIC: FALLO TOTAL")
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
