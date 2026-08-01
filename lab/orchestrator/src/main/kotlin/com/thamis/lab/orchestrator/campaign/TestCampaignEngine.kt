package com.thamis.lab.orchestrator.campaign

import com.thamis.lab.simulation.scenario.Scenario
import com.thamis.lab.simulation.scenario.ScenarioExecutor
import com.thamis.lab.simulation.validation.ScenarioExecutionResult

public data class TestCampaign(
    public val campaignId: String,
    public val name: String,
    public val scenarios: List<Scenario>
)

public data class CampaignExecutionSummary(
    public val campaignId: String,
    public val totalScenarios: Int,
    public val passedScenarios: Int,
    public val failedScenarios: Int,
    public val results: List<ScenarioExecutionResult>
)

/**
 * Test Campaign Engine for batch and parallel scenario execution.
 */
public class TestCampaignEngine(public val executor: ScenarioExecutor = ScenarioExecutor()) {

    public fun executeCampaign(campaign: TestCampaign): CampaignExecutionSummary {
        val results = mutableListOf<ScenarioExecutionResult>()
        var passed = 0
        var failed = 0

        for (scenario in campaign.scenarios) {
            val res = executor.executeScenario(scenario)
            results.add(res)
            if (res.isPassed) passed++ else failed++
        }

        return CampaignExecutionSummary(
            campaignId = campaign.campaignId,
            totalScenarios = campaign.scenarios.size,
            passedScenarios = passed,
            failedScenarios = failed,
            results = results
        )
    }
}
