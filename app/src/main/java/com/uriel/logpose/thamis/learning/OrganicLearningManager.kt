package com.uriel.logpose.thamis.learning

import com.uriel.logpose.core.compat.core.LogPoseLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

/**
 * OrganicLearningManager: Triggers autonomous code-generation tasks to the PC Brain.
 * Part of the Singularity Bucle v60.0.
 */
object OrganicLearningManager {

    private val scope = CoroutineScope(Dispatchers.IO)
    private const val BRAIN_URL = "http://localhost:5000/chat"

    /**
     * Request the PC Agent to analyze a phrase and inject a new rule into ActionMapper.kt.
     */
    fun requestRuleSynthesis(phrase: String) {
        LogPoseLogger.i("OrganicLearning", "Requesting Rule Synthesis for: '$phrase'")
        
        scope.launch {
            try {
                val url = URL(BRAIN_URL)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true

                val payload = """
                    {
                        "msg": "LEARNING_TASK: The app failed to understand the phrase '$phrase'. Generate a new Kotlin 'when' branch for ActionMapper.kt to handle this intent and variants. Inject it using write_patch."
                    }
                """.trimIndent()

                conn.outputStream.write(payload.toByteArray())
                val responseCode = conn.responseCode
                LogPoseLogger.d("OrganicLearning", "Brain accepted task. Response: ${responseCode}")
            } catch (e: Exception) {
                LogPoseLogger.w("OrganicLearning", "Failed to reach PC Brain for rule synthesis: ${e.message}")
            }
        }
    }
}
