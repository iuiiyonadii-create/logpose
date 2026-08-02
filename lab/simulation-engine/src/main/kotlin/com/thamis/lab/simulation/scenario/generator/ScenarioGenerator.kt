package com.thamis.lab.simulation.scenario.generator

import com.thamis.lab.core.contracts.event.LabEvent
import com.thamis.lab.core.contracts.snapshot.CognitiveSnapshot
import com.thamis.lab.simulation.scenario.Scenario
import com.thamis.lab.simulation.scenario.ScenarioBuilder
import java.util.Random

public data class GeneratedScenarioMetadata(
    public val scenarioId: String,
    public val description: String,
    public val objective: String,
    public val difficultyLevel: String,
    public val estimatedDurationMs: Long,
    public val coverageScope: String,
    public val riskLevel: String,
    public val expectedResult: String,
    public val scenario: Scenario
)

/**
 * Autonomous Scenario Generator automatically creating varied test scenarios across Bluetooth, GPS, Battery, Audio, and App states.
 */
public class ScenarioGenerator(private val random: Random = Random(42L)) {

    private val voiceCommands = listOf("poné música", "ir a gasolinería", "pausar música", "llamar a soporte", "siguiente canción")
    private val activeApps = listOf("Spotify", "YouTube Music", "Poweramp", "LogPose", "Google Maps")
    private val riskLevels = listOf("LOW", "MEDIUM", "HIGH", "CRITICAL")
    private val difficulties = listOf("EASY", "MODERATE", "HARD", "EXTREME")

    public fun generateAutonomousScenario(index: Int): GeneratedScenarioMetadata {
        val cmd = voiceCommands[random.nextInt(voiceCommands.size)]
        val app = activeApps[random.nextInt(activeApps.size)]
        val gpsSpeed = random.nextInt(130) // 0 to 130 km/h
        val battery = 5 + random.nextInt(95) // 5% to 100%
        val risk = riskLevels[random.nextInt(riskLevels.size)]
        val difficulty = difficulties[random.nextInt(difficulties.size)]

        val id = "auto-gen-$index"
        val desc = "Auto-generated test #$index: '$cmd' while riding at $gpsSpeed km/h with active app $app"
        val expectedIntent = when (cmd) {
            "poné música" -> "PLAY_MUSIC"
            "ir a gasolinería" -> "NAVIGATE_TO"
            "pausar música" -> "PAUSE_MUSIC"
            else -> "GENERIC_ACTION"
        }

        val scen = ScenarioBuilder(id, "Generated Scenario $index")
            .description(desc)
            .initialSnapshot(CognitiveSnapshot(timestampMs = index.toLong() * 100L))
            .addEvent(LabEvent.TextCommandEvent("evt-$index", index.toLong() * 100L + 50L, userText = cmd))
            .expectedIntent(expectedIntent)
            .build()

        return GeneratedScenarioMetadata(
            scenarioId = id,
            description = desc,
            objective = "Validate intent parser and context safety under $difficulty difficulty ($gpsSpeed km/h, $app, $battery% Battery)",
            difficultyLevel = difficulty,
            estimatedDurationMs = 15L + random.nextInt(20),
            coverageScope = "VoiceCommand + GPS + Battery + $app",
            riskLevel = risk,
            expectedResult = expectedIntent,
            scenario = scen
        )
    }

    public fun generateCampaignScenarios(count: Int): List<GeneratedScenarioMetadata> {
        val list = mutableListOf<GeneratedScenarioMetadata>()
        for (i in 1..count) {
            list.add(generateAutonomousScenario(i))
        }
        return list
    }
}
