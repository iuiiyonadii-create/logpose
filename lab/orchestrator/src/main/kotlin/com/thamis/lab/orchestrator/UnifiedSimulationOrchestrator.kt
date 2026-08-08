package com.thamis.lab.orchestrator

import com.thamis.lab.core.common.result.LabResult
import com.thamis.lab.intelligence.core.AiAnalysisEngine
import com.thamis.lab.intelligence.engineering.QualityEngine
import com.thamis.lab.intelligence.engineering.IntelligenceReportGenerator
import com.thamis.lab.orchestrator.campaign.CampaignExecutionSummary
import com.thamis.lab.orchestrator.campaign.TestCampaign
import com.thamis.lab.orchestrator.campaign.TestCampaignEngine
import com.thamis.lab.orchestrator.logpose.LogPoseIntegrationLayer
import com.thamis.lab.performance.device.DeviceInfo
import com.thamis.lab.performance.device.DeviceRegistry
import kotlinx.coroutines.*

public data class FullOrchestrationReport(
    public val campaignSummary: CampaignExecutionSummary,
    public val markdownReport: String,
    public val overallQualityScore: Double
)

/**
 * Unified Simulation Orchestrator - The central operational brain of THAMIS LAB.
 */
public class UnifiedSimulationOrchestrator(
    public val deviceRegistry: DeviceRegistry = DeviceRegistry(),
    public val integrationLayer: LogPoseIntegrationLayer = LogPoseIntegrationLayer(),
    public val campaignEngine: TestCampaignEngine = TestCampaignEngine(),
    public val aiAnalyzer: AiAnalysisEngine = AiAnalysisEngine(),
    public val qualityEngine: QualityEngine = QualityEngine(),
    public val reportGenerator: IntelligenceReportGenerator = IntelligenceReportGenerator()
) {
    public fun runEndToEndCampaign(
        device: DeviceInfo,
        apkPath: String,
        campaign: TestCampaign
    ): LabResult<FullOrchestrationReport> {
        com.thamis.lab.core.common.telemetry.LabTelemetry.logEvent("Orchestrator", "Starting campaign: ${campaign.campaignId}")
        // 1. Register Device
        deviceRegistry.registerDevice(device)

        // 2. Deploy LogPose
        val sessionResult = integrationLayer.deployAndLaunch(device, apkPath)
        if (sessionResult.isFailure) {
            return LabResult.Failure(sessionResult.errorOrNull()!!)
        }
        val session = sessionResult.getOrNull()!!

        // 3. Execute Campaign Scenarios
        val summary = campaignEngine.executeCampaign(campaign)

        // 4. AI & Quality Evaluation
        val qualityScore = qualityEngine.calculateScores(
            passedScenarios = summary.passedScenarios,
            totalScenarios = summary.totalScenarios,
            faultScenariosPassed = summary.passedScenarios,
            totalFaultScenarios = summary.totalScenarios
        )

        // 5. Generate Reports
        val markdown = reportGenerator.generateMarkdownReport(qualityScore, emptyList())

        // 6. Cleanup Session
        integrationLayer.stopSession(session.sessionId)

        return LabResult.Success(
            FullOrchestrationReport(
                campaignSummary = summary,
                markdownReport = markdown,
                overallQualityScore = qualityScore.overallScore
            )
        )
    }
}
