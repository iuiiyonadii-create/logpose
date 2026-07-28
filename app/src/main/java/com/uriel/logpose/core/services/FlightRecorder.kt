package com.uriel.logpose.core.services

import android.content.Context
import android.media.AudioManager
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.features.voice.VoskVoiceEngine
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * FlightRecorder: Sistema de registro persistente para validación en el mundo real.
 * Diseñado para el HITO 1: Corrida de 20 minutos con hardware real.
 */
object FlightRecorder {

    private const val TAG = "ThamisFlightRecorder"
    private const val LOG_FILE_NAME = "real_world_test.json"
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var recordingJob: Job? = null
    private lateinit var logFile: File
    
    private val commandHistory = mutableListOf<CommandRecord>()

    data class CommandRecord(
        val text: String,
        val success: Boolean,
        val timestamp: Long = System.currentTimeMillis()
    )

    fun initialize(context: Context) {
        logFile = File(context.filesDir, LOG_FILE_NAME)
        if (!logFile.exists()) {
            logFile.writeText("[]") // Iniciar como array JSON vacío
        }
        LogPoseLogger.i("$TAG: Inicializado. Archivo en: ${logFile.absolutePath}")
    }

    fun startSession() {
        LogPoseLogger.i("$TAG: Iniciando sesión de grabación (30s snapshots).")
        commandHistory.clear()
        recordingJob?.cancel()
        recordingJob = scope.launch {
            while (isActive) {
                takeSnapshot()
                delay(30_000)
            }
        }
    }

    fun stopSession() {
        LogPoseLogger.i("$TAG: Finalizando sesión de grabación.")
        recordingJob?.cancel()
        // Snapshot final
        scope.launch { takeSnapshot() }
    }

    /**
     * Registra el resultado de un comando de voz real.
     */
    fun logCommand(text: String, success: Boolean) {
        val record = CommandRecord(text, success)
        commandHistory.add(record)
        LogPoseLogger.i("$TAG: Comando registrado -> '$text' (Éxito: $success)")
    }

    private fun takeSnapshot() {
        try {
            val audioManager = LogPoseCallService.instance?.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            val isScoOn = audioManager?.isBluetoothScoOn ?: false
            
            val snapshot = JSONObject().apply {
                put("timestamp", System.currentTimeMillis())
                put("time_readable", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))
                put("trip_status", LogPoseCallService.instance?.tripStatus?.value?.name ?: "UNKNOWN")
                put("sco_active", isScoOn)
                put("noise_level", VoskVoiceEngine.getAmbientNoiseLevel())
                put("free_memory_mb", Runtime.getRuntime().freeMemory() / (1024 * 1024))
                
                // Comandos desde el último snapshot
                val commands = JSONArray()
                commandHistory.forEach { 
                    commands.put(JSONObject().apply {
                        put("text", it.text)
                        put("success", it.success)
                    })
                }
                put("recent_commands", commands)
            }

            // Persistir a disco (Append al array JSON)
            val currentContent = logFile.readText()
            val array = JSONArray(currentContent)
            array.put(snapshot)
            logFile.writeText(array.toString(2))
            
            // Limpiar historial de comandos tras snapshot exitoso
            commandHistory.clear()

        } catch (e: Exception) {
            LogPoseLogger.e("$TAG: Error al guardar snapshot: ${e.message}")
        }
    }

    fun getLogFile(): File = logFile
}
