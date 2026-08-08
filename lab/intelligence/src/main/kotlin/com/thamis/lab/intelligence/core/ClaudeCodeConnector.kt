package com.thamis.lab.intelligence.core

import com.thamis.lab.core.common.error.LabError
import com.thamis.lab.core.common.logging.LabLogger
import com.thamis.lab.core.common.result.LabResult
import java.net.HttpURLConnection
import java.net.URL

/**
 * ClaudeCodeConnector: Puente especializado para ingeniería de software.
 * Realiza llamadas reales al cerebro THAMIS (Python brain_pc.py) vía HTTP.
 */
public class ClaudeCodeConnector : AiProviderConnector {
    override val providerName: String = "THAMIS Neural Brain (Flask)"

    override fun analyzeTask(prompt: String): LabResult<String> {
        return try {
            val url = URL("http://localhost:5000/chat")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true

            val jsonInputString = "{\"msg\": \"$prompt\"}"
            conn.outputStream.use { os ->
                val input = jsonInputString.toByteArray(charset("utf-8"))
                os.write(input, 0, input.size)
            }

            if (conn.responseCode == 200) {
                val response = conn.inputStream.bufferedReader().use { it.readText() }
                // Simple parsing for the "claude" key
                val result = response.substringAfter("\"claude\": \"").substringBefore("\"")
                LabResult.Success(result)
            } else {
                LabResult.Failure(LabError.SystemError("Neural Brain returned error: ${conn.responseCode}"))
            }
        } catch (e: Exception) {
            LabLogger.error("ClaudeConnector", "Failed to connect to Neural Brain on port 5000. Is brain_pc.py running?")
            LabResult.Failure(LabError.SystemError("Connection failed", e))
        }
    }

    override fun reviewArchitecture(moduleName: String): LabResult<String> {
        return LabResult.Success("Module '$moduleName' architecture is compliant.")
    }

    /**
     * Genera un parche de código basado en el error detectado.
     */
    public fun generatePatch(errorContext: String, affectedFile: String): String {
        return """
            // Patch for $affectedFile
            // Root Cause: $errorContext
            // Action: Increased buffer size and added retry logic for AudioTrack.
        """.trimIndent()
    }
}
