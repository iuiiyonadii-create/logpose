package com.thamis.lab.simulation.scenario.generator

public data class CoverageReport(
    public val totalScenariosAnalyzed: Int,
    public val featureCoveragePercent: Double,
    public val bluetoothCoveragePercent: Double,
    public val gpsCoveragePercent: Double,
    public val audioCoveragePercent: Double,
    public val batteryCoveragePercent: Double
)

/**
 * Scenario Coverage Analyzer for measuring event, feature, and device parameter coverage across campaigns.
 */
public class ScenarioCoverageAnalyzer {

    public fun analyzeCoverage(scenarios: List<GeneratedScenarioMetadata>): CoverageReport {
        if (scenarios.isEmpty()) {
            return CoverageReport(0, 0.0, 0.0, 0.0, 0.0, 0.0)
        }

        val total = scenarios.size
        val btCount = scenarios.count { it.coverageScope.contains("Bluetooth") || it.description.contains("intercom") }
        val gpsCount = scenarios.count { it.coverageScope.contains("GPS") || it.description.contains("km/h") }
        val audioCount = scenarios.count { it.coverageScope.contains("VoiceCommand") || it.description.contains("música") }
        val batteryCount = scenarios.count { it.coverageScope.contains("Battery") || it.description.contains("Battery") }

        val btCov = (btCount.toDouble() / total) * 100.0
        val gpsCov = (gpsCount.toDouble() / total) * 100.0
        val audioCov = (audioCount.toDouble() / total) * 100.0
        val batCov = (batteryCount.toDouble() / total) * 100.0

        val overall = (btCov + gpsCov + audioCov + batCov) / 4.0

        return CoverageReport(
            totalScenariosAnalyzed = total,
            featureCoveragePercent = overall,
            bluetoothCoveragePercent = btCov,
            gpsCoveragePercent = gpsCov,
            audioCoveragePercent = audioCov,
            batteryCoveragePercent = batCov
        )
    }
}
