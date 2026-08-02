package com.thamis.lab.evidence

import com.thamis.lab.evidence.exporter.ComprehensiveReportExporter
import org.junit.Assert.assertNotNull
import org.junit.Test

class ExporterTest {

    @Test
    fun testComprehensiveReportExporter() {
        val exporter = ComprehensiveReportExporter()
        val expFiles = exporter.exportComprehensiveReport("Executive Summary", "Summary content")
        assertNotNull(expFiles.markdownPath)
        assertNotNull(expFiles.htmlPath)
        assertNotNull(expFiles.jsonPath)
    }
}
