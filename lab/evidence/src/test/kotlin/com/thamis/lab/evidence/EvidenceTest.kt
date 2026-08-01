package com.thamis.lab.evidence

import com.thamis.lab.evidence.archive.CampaignArchive
import com.thamis.lab.evidence.certification.CertificationEngine
import com.thamis.lab.evidence.certification.CertificationLevel
import com.thamis.lab.evidence.model.EvidenceStatus
import com.thamis.lab.evidence.model.ExecutionEvidence
import com.thamis.lab.evidence.reports.EvidenceReportExporter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EvidenceTest {

    @Test
    fun testEvidenceArchivingAndTraceability() {
        val archive = CampaignArchive()
        val evidence = ExecutionEvidence(
            gitCommit = "c1a2b3c",
            deviceId = "pixel-8",
            androidApi = 34,
            scenarioId = "scen-100",
            executionDurationMs = 15L,
            qualityScore = 100.0,
            cpuPercent = 10.0,
            ramMb = 128.0,
            status = EvidenceStatus.PASSED
        )

        archive.archiveEvidence(evidence)

        val retrieved = archive.getEvidenceByUuid(evidence.evidenceUuid)
        assertNotNull(retrieved)
        assertEquals("c1a2b3c", retrieved?.gitCommit)
        assertEquals(34, retrieved?.androidApi)
    }

    @Test
    fun testCertificationEngineEvaluation() {
        val certEngine = CertificationEngine()
        val list = mutableListOf<ExecutionEvidence>()

        for (i in 1..100) {
            list.add(
                ExecutionEvidence(
                    deviceId = "dev-$i",
                    androidApi = 34,
                    scenarioId = "scen-$i",
                    executionDurationMs = 10L,
                    qualityScore = 98.0,
                    cpuPercent = 5.0,
                    ramMb = 100.0,
                    status = EvidenceStatus.PASSED
                )
            )
        }

        val level = certEngine.evaluateCertification(list)
        assertEquals(CertificationLevel.READY_FOR_PRODUCTION, level)
    }

    @Test
    fun testEvidenceReportExporter() {
        val exporter = EvidenceReportExporter()
        val evidence = ExecutionEvidence(
            gitCommit = "HEAD",
            deviceId = "emulator-5554",
            androidApi = 34,
            scenarioId = "scen-test",
            executionDurationMs = 20L,
            qualityScore = 100.0,
            cpuPercent = 8.0,
            ramMb = 120.0
        )

        val markdown = exporter.exportMarkdown(listOf(evidence), CertificationLevel.READY_FOR_PRODUCTION)
        assertNotNull(markdown)
        assertTrue(markdown.contains("READY_FOR_PRODUCTION"))
        assertTrue(markdown.contains("emulator-5554"))
    }
}
