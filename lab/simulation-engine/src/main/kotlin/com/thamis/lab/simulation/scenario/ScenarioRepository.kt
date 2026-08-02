package com.thamis.lab.simulation.scenario

import java.util.concurrent.ConcurrentHashMap

/**
 * Thread-safe Repository for storing and retrieving Scenarios.
 */
public class ScenarioRepository {
    private val scenarios = ConcurrentHashMap<String, Scenario>()

    public fun save(scenario: Scenario) {
        scenarios[scenario.scenarioId] = scenario
    }

    public fun getById(scenarioId: String): Scenario? {
        return scenarios[scenarioId]
    }

    public fun getAll(): List<Scenario> {
        return scenarios.values.toList()
    }

    public fun clear() {
        scenarios.clear()
    }
}
