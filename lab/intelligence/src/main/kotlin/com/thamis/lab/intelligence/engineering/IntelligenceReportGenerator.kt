package com.thamis.lab.intelligence.engineering

import java.util.Locale

/**
 * Generates Executive, Technical, Markdown, and JSON intelligence reports.
 */
public class IntelligenceReportGenerator {

    public fun generateMarkdownReport(
        qualityScore: QualityScore,
        recommendations: List<FixRecommendation>
    ): String {
        val sb = StringBuilder()
        sb.appendLine("# 🧠 THAMIS Lab Intelligence Report")
        sb.appendLine("## Quality Summary")
        sb.appendLine("- **Overall Score**: ${String.format(Locale.US, "%.2f", qualityScore.overallScore)} / 100")
        sb.appendLine("- **Reliability**: ${String.format(Locale.US, "%.2f", qualityScore.reliabilityScore)}%")
        sb.appendLine("- **Robustness**: ${String.format(Locale.US, "%.2f", qualityScore.robustnessScore)}%")
        sb.appendLine()
        sb.appendLine("## Recommendations")
        if (recommendations.isEmpty()) {
            sb.appendLine("No critical issues or fixes suggested.")
        } else {
            for (rec in recommendations) {
                sb.appendLine("- [${rec.riskLevel}] **${rec.targetComponent}**: ${rec.suggestedAction}")
            }
        }
        return sb.toString()
    }
}
