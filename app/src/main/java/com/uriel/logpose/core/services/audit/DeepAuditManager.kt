package com.uriel.logpose.core.services.audit

import com.uriel.logpose.core.compat.core.LogPoseLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

/**
 * DeepAuditManager: The "Black Box" of LogPose.
 * v60.0: Captures and sends full forensic data to the Agent upon session end.
 */
object DeepAuditManager {

    private val scope = CoroutineScope(Dispatchers.IO)
    private fun sendForensicToLab(report: String) {
        val pcIp = com.uriel.logpose.core.parser.LabDiscoveryService.pcIp.value ?: return
        val brainUrl = "http://$pcIp:5000/chat"
        
        scope.launch {
            try {
                // 1. Capture Real Session Logs
                val logcatDump = LogPoseLogger.getRecentLogs(200)

                // 2. Send to Brain
                val url = URL(brainUrl)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true

                val payload = """
                    {
                        "msg": "FORENSIC_TASK: Session ended. Perform deep root-cause analysis on logs. find real error, patch code and redeploy.",
                        "logs": "$logcatDump"
                    }
                """.trimIndent()

                conn.outputStream.write(payload.toByteArray())
                val code = conn.responseCode
                LogPoseLogger.i("DeepAudit", "Forensic data transmitted. Brain response code: $code")
                
            } catch (e: Exception) {
                LogPoseLogger.e("DeepAudit", "Failed to send forensic data: ${e.message}")
            }
        }
    }
}
