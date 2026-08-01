package com.thamis.lab.evidence

import com.thamis.lab.evidence.excellence.EngineeringExcellenceScoreCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ExcellenceTest {

    @Test
    fun testEngineeringExcellenceScoreCalculator() {
        val calculator = EngineeringExcellenceScoreCalculator()
        val score = calculator.calculateExcellenceScore()

        assertNotNull(score)
        assertEquals(100.0, score.overallExcellenceScore, 0.01)
        assertEquals(100.0, score.architectureExcellenceScore, 0.01)
    }
}
