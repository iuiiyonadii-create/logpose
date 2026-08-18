package com.thamis.lab.core.common.ai

import com.thamis.lab.core.common.result.LabResult
import com.thamis.lab.core.common.error.LabError
import java.net.HttpURLConnection
import java.net.URL

/**
 * AgenticResponse: Unified response format for all AI interactions.
 */
public data class AgenticResponse(
    public val output: String,
    public val thinking: String = "",
    public val toolCalls: List<String> = emptyList()
)

/**
 * Interface for connecting with AI providers.
 */
public interface AiProviderConnector {
    public val providerName: String
    public fun analyzeTask(prompt: String): LabResult<AgenticResponse>
    public fun reviewArchitecture(moduleName: String): LabResult<String>
}

/**
 * Default Http Connector for reaching the Thamis PC Brain.
 */
public open class ThamisHttpConnector : AiProviderConnector {
    override val providerName: String = "Thamis Neural Brain (Http)"

    override fun analyzeTask(prompt: String): LabResult<AgenticResponse> {
        return try {
            val url = URL("http://localhost:5000/chat")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true

            val jsonInputString = "{\"msg\": \"$prompt\"}"
            conn.outputStream.use { os ->
                os.write(jsonInputString.toByteArray(charset("utf-8")))
            }

            if (conn.responseCode == 200) {
                val response = conn.inputStream.bufferedReader().use { it.readText() }
                val thinking = response.substringAfter("\"thinking\": \"").substringBefore("\"")
                val result = response.substringAfter("\"claude\": \"").substringBefore("\"")
                LabResult.Success(AgenticResponse(output = result, thinking = thinking))
            } else {
                LabResult.Failure(LabError.SystemError("Brain error: ${conn.responseCode}"))
            }
        } catch (e: Exception) {
            LabResult.Failure(LabError.SystemError("Connection failed", e))
        }
    }

    override fun reviewArchitecture(moduleName: String): LabResult<String> {
        return LabResult.Success("Compliant.")
    }
}
