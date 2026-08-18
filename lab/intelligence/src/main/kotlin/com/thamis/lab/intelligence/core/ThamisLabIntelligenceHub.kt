package com.thamis.lab.intelligence.core

import com.thamis.lab.core.common.logging.LabLogger
import com.thamis.lab.intelligence.engineering.*
import com.thamis.lab.intelligence.security.*
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
    public val excellenceConstitution: EngineeringExcellenceConstitution = EngineeringExcellenceConstitution(),
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
        
        // v65.0: Pass specific context to the Excellence Constitution
        val excelReport = excellenceConstitution.auditExcellenceConstitution("Full Repository Scan v65.0")
        
        // Hunt for bugs in the current context
        val bugs = bugHunter.huntForBugs("System nominal scan", 0)
        val bugSummary = if (bugs.isEmpty()) "No active bugs detected." else "${bugs.size} bugs identified."

        return """
            | --- 🧠 THAMIS CONSOLIDATED AUDIT ---
            | SECURITY: ${secReport.auditSummary}
            | ARCHITECTURE ENTROPY: ${entReport.summary}
            | EXCELLENCE CONSTITUTION: ${if (excelReport.isExcellenceConstitutionProtected) "PROTECTED" else "VIOLATED"}
            | BUG HUNTER: $bugSummary
            | QUALITY SCORE: ${qualReport.overallScore}/100
            | STATUS: ${if (excelReport.isExcellenceConstitutionProtected) "NOMINAL" else "GOVERNANCE_FAIL"}
        """.trimMargin()
    }

    /**
     * Triggers external research via Agent-Reach (Python side).
     */
    public fun researchExternalSolution(topic: String): String {
        LabLogger.info(TAG, "Requesting external research via Agent-Reach for: $topic")
        val result = repairEngine.aiConnector.analyzeTask("research: $topic")
        return result.getOrNull()?.output ?: "Research failed. Check Python Brain connection."
    }

    /**
     * Triggers the Evolutionary Self-Deployment loop.
     * Completes the Singularity loop: Build -> Deploy -> Restart.
     */
    public fun triggerSelfDeployment(): String {
        LabLogger.warn(TAG, "Initiating SELF-DEPLOYMENT (Singularity v60.0)...")
        
        // 1. Request Build
        val buildResult = repairEngine.aiConnector.analyzeTask("CMD: build_project")
        if (buildResult.isFailure || buildResult.getOrNull()?.output?.contains("FAILED") == true) {
            return "Build failed. Deployment aborted."
        }

        // 2. Request Deploy
        val deployResult = repairEngine.aiConnector.analyzeTask("CMD: deploy_to_device")
        return deployResult.getOrNull()?.output ?: "Deployment failed."
    }
}
