package com.thamis.lab.evidence

import com.thamis.lab.evidence.quality.ThamisQualityScoreCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ThamisQualityScoreTest {

    @Test
    fun testThamisQualityScoreCalculator() {
        val qualityCalculator = ThamisQualityScoreCalculator()
        val quality = qualityCalculator.calculateCompositeQualityScore()

        assertNotNull(quality)
        assertEquals(100.0, quality.repositoryHealthScore, 0.01)
        assertEquals(100.0, quality.architectureScore, 0.01)
    }
}
