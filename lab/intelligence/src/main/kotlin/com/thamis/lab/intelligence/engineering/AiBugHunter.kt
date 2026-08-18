package com.thamis.lab.intelligence.engineering

import com.thamis.lab.core.common.logging.LabLogger
import com.thamis.lab.core.common.ai.*

public data class DiscoveredBugReport(
    public val bugId: String,
    public val title: String,
    public val category: String,
    public val rootCause: String,
    public val priority: String,
    public val impactScore: Double,
    public val nonIntrusiveRecommendation: String
)

/**
 * AI Bug Hunter for non-intrusively discovering, clustering, classifying, and explaining root cause of software defects.
 */
public class AiBugHunter(
    private val aiConnector: AiProviderConnector = com.thamis.lab.intelligence.core.ClaudeCodeConnector()
) {
    private val TAG = "AiBugHunter"

    public fun huntForBugs(rawLogcat: String, crashesCount: Int): List<DiscoveredBugReport> {
        LabLogger.info(TAG, "Hunting for bugs across logcat traces (crashes: $crashesCount)...")
        
        if (crashesCount == 0 && !rawLogcat.contains("E/")) {
            return emptyList()
        }

        // v60.0: Real Agentic Log Analysis
        val prompt = """
            Analyze the following Android Logcat segment. 
            Identify unique bugs, cluster related entries, and suggest root causes.
            Total Crashes reported by system: $crashesCount
            Logcat:
            $rawLogcat
        """.trimIndent()

        val aiResult = aiConnector.analyzeTask(prompt)
        
        return if (aiResult.isSuccess) {
            val response = aiResult.getOrNull()
            // Map the agentic response to a structured bug report
            listOf(
                DiscoveredBugReport(
                    bugId = "agent-bug-${System.currentTimeMillis()}",
                    title = "Agent-Detected Issue",
                    category = "SYSTEM_ANALYSIS",
                    rootCause = response?.output ?: "Unknown Cause",
                    priority = if (crashesCount > 0) "HIGH" else "MEDIUM",
                    impactScore = if (crashesCount > 0) 8.5 else 4.0,
                    nonIntrusiveRecommendation = "Review thinking block in Mission Control for detailed trace analysis."
                )
            )
        } else {
            emptyList()
        }
    }
}
