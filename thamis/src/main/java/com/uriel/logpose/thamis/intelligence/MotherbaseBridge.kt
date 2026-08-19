package com.uriel.logpose.thamis.intelligence

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.parser.LabDiscoveryService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * MotherbaseBridge: High-speed link to the PC Engineering Agent (ClauCode).
 * v73.0: Bidirectional reasoning and system health reporting.
 */
object MotherbaseBridge {

    private const val PORT = 5000

    /**
     * Reports missing assets to the Motherbase for autonomous download suggestions.
     */
    suspend fun reportSystemHealth(missingWhisper: Boolean, missingGemma: Boolean) = withContext(Dispatchers.IO) {
        val pcIp = LabDiscoveryService.pcIp.value ?: return@withContext
        try {
            val url = URL("http://$pcIp:$PORT/chat")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true
            
            val report = JSONObject().apply {
                put("msg", "STATUS_REPORT")
                put("missing_whisper", missingWhisper)
                put("missing_gemma", missingGemma)
            }
            
            conn.outputStream.use { it.write(report.toString().toByteArray()) }
            if (conn.responseCode == 200) {
                LogPoseLogger.i("Motherbase", "Salud del sistema reportada al PC.")
            }
        } catch (e: Exception) {
            LogPoseLogger.d("Motherbase", "PC Agent offline. Skipping health report.")
        }
    }

    /**
     * Offloads reasoning to the PC Agent when the local LLM is confused or missing.
     */
    suspend fun queryReasoning(dirtyText: String): JSONObject? = withContext(Dispatchers.IO) {
        val pcIp = LabDiscoveryService.pcIp.value ?: return@withContext null
        
        try {
            val url = URL("http://$pcIp:$PORT/chat")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true
            
            val payload = JSONObject().apply {
                put("msg", dirtyText)
            }
            
            conn.outputStream.use { it.write(payload.toString().toByteArray()) }
            
            if (conn.responseCode == 200) {
                val response = conn.inputStream.bufferedReader().use { it.readText() }
                return@withContext JSONObject(response)
            }
        } catch (e: Exception) {
            LogPoseLogger.e("Motherbase", "Fallo en consulta a PC: ${e.message}")
        }
        return@withContext null
    }
}
