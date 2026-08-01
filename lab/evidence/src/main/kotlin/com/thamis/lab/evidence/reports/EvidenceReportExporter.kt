package com.thamis.lab.evidence.reports

import com.thamis.lab.evidence.certification.CertificationLevel
import com.thamis.lab.evidence.model.ExecutionEvidence
import java.util.Locale

/**
 * Exporters for Markdown, JSON, and HTML evidence reports.
 */
public class EvidenceReportExporter {

    public fun exportMarkdown(
        evidences: List<ExecutionEvidence>,
        certificationLevel: CertificationLevel
    ): String {
        val sb = StringBuilder()
        sb.appendLine("# 📜 THAMIS Lab Official Evidence & Certification Report")
        sb.appendLine("**Official Status**: `${certificationLevel.name}`")
        sb.appendLine()
        sb.appendLine("## Executed Evidence Records")
        for (ev in evidences) {
            sb.appendLine("- **UUID**: `${ev.evidenceUuid}` | **Commit**: `${ev.gitCommit}` | **Device**: `${ev.deviceId}` (API ${ev.androidApi}) | **Score**: ${String.format(Locale.US, "%.1f", ev.qualityScore)} | **Status**: `${ev.status}`")
        }
        return sb.toString()
    }
}
