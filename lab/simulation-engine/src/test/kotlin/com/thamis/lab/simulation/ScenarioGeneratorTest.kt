package com.thamis.lab.simulation

import com.thamis.lab.simulation.scenario.generator.ScenarioCoverageAnalyzer
import com.thamis.lab.simulation.scenario.generator.ScenarioGenerator
import com.thamis.lab.simulation.scenario.generator.ScenarioMutator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ScenarioGeneratorTest {

    @Test
    fun testAutonomousScenarioGeneratorAndCoverage() {
        val generator = ScenarioGenerator()
        val analyzer = ScenarioCoverageAnalyzer()

        val count = 50
        val campaignMetas = generator.generateCampaignScenarios(count)

        assertEquals(count, campaignMetas.size)

        val firstMeta = campaignMetas.first()
        assertNotNull(firstMeta.scenarioId)
        assertNotNull(firstMeta.description)
        assertNotNull(firstMeta.objective)
        assertNotNull(firstMeta.difficultyLevel)
        assertNotNull(firstMeta.riskLevel)
        assertTrue(firstMeta.estimatedDurationMs > 0L)

        val coverage = analyzer.analyzeCoverage(campaignMetas)
        assertEquals(count, coverage.totalScenariosAnalyzed)
        assertTrue(coverage.featureCoveragePercent > 0.0)
    }

    @Test
    fun testScenarioMutator() {
        val generator = ScenarioGenerator()
        val mutator = ScenarioMutator()

        val meta = generator.generateAutonomousScenario(1)
        val mutated = mutator.mutateScenario(meta.scenario, "m-1")

        assertNotNull(mutated)
        assertTrue(mutated.scenarioId.contains("mutated-m-1"))
        assertTrue(mutated.events.size > meta.scenario.events.size)
    }
}
