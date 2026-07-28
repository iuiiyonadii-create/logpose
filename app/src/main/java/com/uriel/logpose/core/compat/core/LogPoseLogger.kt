package com.uriel.logpose.core.compat.core

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * LogPoseLogger: Sistema de registro persistente (Caja Negra).
 */
object LogPoseLogger {
    private const val TAG = "LogPose"
    private var logFile: File? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    fun initialize(context: android.content.Context) {
        val dir = context.getExternalFilesDir(null)
        logFile = File(dir, "logpose_trace.txt")
        
        if ((logFile?.length() ?: 0) > 2 * 1024 * 1024) {
            logFile?.delete()
        }
    }

    fun d(msg: String) {
        Log.d(TAG, msg)
        writeToFile("D", msg)
    }

    fun i(msg: String) {
        Log.i(TAG, msg)
        writeToFile("I", msg)
    }

    fun w(msg: String) {
        Log.w(TAG, msg)
        writeToFile("W", msg)
    }

    fun e(msg: String) {
        Log.e(TAG, msg)
        writeToFile("E", msg)
    }

    /**
     * Log de telemetría para Kimi: [Said, Heard, Speed, Result]
     */
    fun kimiTelemetry(said: String, heard: String, speed: Int, success: Boolean) {
        val status = if (success) "✅ OK" else "❌ FAIL"
        val msg = "KIMI_DEBUG | Said: '$said' | Heard: '$heard' | Speed: ${speed}km/h | Result: $status"
        i(msg)
    }

    private fun writeToFile(level: String, msg: String) {
        scope.launch {
            try {
                logFile?.let { file ->
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
                    val timestamp = sdf.format(Date())
                    val line = "[$timestamp] [$level] $msg\n"
                    FileOutputStream(file, true).use { it.write(line.toByteArray()) }
                }
            } catch (e: Exception) {}
        }
    }
}
