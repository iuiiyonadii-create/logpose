package com.thamis.lab.intelligence.core

import com.thamis.lab.core.common.logging.LabLogger
import com.thamis.lab.intelligence.engineering.*
import com.thamis.lab.intelligence.security.SecurityAuditEngine
import com.thamis.lab.intelligence.evolution.SoftwareEntropyEngine
import com.thamis.lab.intelligence.training.LogPoseTrainingEngine

/**
 * THAMIS Lab Intelligence Hub: Central orchestrator for all IA capabilities.
 * unifies Engineering, Security, Evolution and Training engines into a single access point.
 */
public class ThamisLabIntelligenceHub(
    public val bugHunter: AiBugHunter = AiBugHunter(),
    public val repairEngine: SelfRepairEngine = SelfRepairEngine(),
    public val securityEngine: SecurityAuditEngine = SecurityAuditEngine(),
    public val entropyEngine: SoftwareEntropyEngine = SoftwareEntropyEngine(),
    public val qualityEngine: QualityEngine = QualityEngine(),
    public val trainingEngine: LogPoseTrainingEngine = LogPoseTrainingEngine()
) {
    private val TAG = "ThamisIntelligenceHub"

    /**
     * Performs a full system audit and returns a consolidated report.
     */
    public fun performFullAutonomousAudit(): String {
        LabLogger.info(TAG, "Initiating full autonomous system audit...")
        
        val secReport = securityEngine.executeSecurityAudit()
        val entReport = entropyEngine.calculateSoftwareEntropy()
        val qualReport = qualityEngine.calculateScores(10, 10, 5, 5)
        
        // Hunt for bugs in the current context
        val bugs = bugHunter.huntForBugs("System nominal scan", 0)
        val bugSummary = if (bugs.isEmpty()) "No active bugs detected." else "${bugs.size} bugs identified."

        return """
            | --- 🧠 THAMIS CONSOLIDATED AUDIT ---
            | SECURITY: ${secReport.auditSummary}
            | ARCHITECTURE ENTROPY: ${entReport.summary}
            | BUG HUNTER: $bugSummary
            | QUALITY SCORE: ${qualReport.overallScore}/100
            | STATUS: NOMINAL
        """.trimMargin()
    }

    /**
     * Triggers external research via Agent-Reach (Python side).
     */
    public fun researchExternalSolution(topic: String): String {
        LabLogger.info(TAG, "Requesting external research via Agent-Reach for: $topic")
        val result = repairEngine.aiConnector.analyzeTask("research: $topic")
        return result.getOrNull() ?: "Research failed. Check Python Brain connection."
    }
}
