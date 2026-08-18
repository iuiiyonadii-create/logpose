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
        // v79.0: Eliminamos URL de debug. En modo producto esto se ignora si no hay bridge.
        return LabResult.Failure(com.thamis.lab.core.common.error.LabError.SystemError("Bridge offline"))
    }

    override fun reviewArchitecture(moduleName: String): LabResult<String> {
        return LabResult.Success("Compliant.")
    }
}
