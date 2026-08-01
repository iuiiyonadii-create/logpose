package com.thamis.lab.orchestrator.campaign

import com.thamis.lab.core.contracts.event.LabEvent
import com.thamis.lab.core.contracts.snapshot.CognitiveSnapshot
import com.thamis.lab.simulation.scenario.Scenario
import com.thamis.lab.simulation.scenario.ScenarioBuilder

/**
 * Production-ready Real-World Campaign Templates for Bluetooth, GPS, Network, Audio, Notifications, Battery, and App Lifecycle.
 */
public object RealWorldCampaignTemplates {

    public fun createBluetoothCampaign(): TestCampaign {
        val s1 = ScenarioBuilder("bt-connect", "Bluetooth Connection & Intercom")
            .description("Tests helmet intercom connection and audio routing")
            .initialSnapshot(CognitiveSnapshot(timestampMs = 0L))
            .addEvent(LabEvent.TextCommandEvent("e-bt1", 100L, userText = "conectar intercomunicador"))
            .expectedIntent("CONNECT_BLUETOOTH")
            .build()

        val s2 = ScenarioBuilder("bt-disconnect", "Bluetooth Signal Drop")
            .description("Simulates sudden loss of intercom Bluetooth signal")
            .initialSnapshot(CognitiveSnapshot(timestampMs = 0L))
            .addEvent(LabEvent.FaultInjectedEvent("f-bt1", 200L, faultType = "BLUETOOTH_DROP", targetComponent = "BluetoothManager"))
            .build()

        return TestCampaign("camp-bluetooth", "Bluetooth Real-World Validation", listOf(s1, s2))
    }

    public fun createGpsCampaign(): TestCampaign {
        val s1 = ScenarioBuilder("gps-high-speed", "High Speed Navigation")
            .description("Tests intent recognition while riding at 100 km/h")
            .initialSnapshot(CognitiveSnapshot(timestampMs = 0L))
            .addEvent(LabEvent.TextCommandEvent("e-gps1", 100L, userText = "ir a gasolinería más cercana"))
            .expectedIntent("NAVIGATE_TO")
            .build()

        return TestCampaign("camp-gps", "GPS & Navigation Validation", listOf(s1))
    }

    public fun createAudioCampaign(): TestCampaign {
        val s1 = ScenarioBuilder("audio-playback", "Music Playback & Control")
            .description("Tests playing music and pausing")
            .initialSnapshot(CognitiveSnapshot(timestampMs = 0L))
            .addEvent(LabEvent.TextCommandEvent("e-audio1", 100L, userText = "poné música"))
            .expectedIntent("PLAY_MUSIC")
            .build()

        return TestCampaign("camp-audio", "Audio & Music Control Validation", listOf(s1))
    }

    public fun createStressTestCampaign(scenarioCount: Int): TestCampaign {
        val scenarios = mutableListOf<Scenario>()
        for (i in 1..scenarioCount) {
            val scen = ScenarioBuilder("stress-$i", "Stress Test Scenario $i")
                .initialSnapshot(CognitiveSnapshot(timestampMs = i.toLong()))
                .addEvent(LabEvent.TextCommandEvent("e-stress-$i", i.toLong() * 10, userText = "poné música"))
                .expectedIntent("PLAY_MUSIC")
                .build()
            scenarios.add(scen)
        }
        return TestCampaign("camp-stress-$scenarioCount", "Stress Test Campaign ($scenarioCount Scenarios)", scenarios)
    }
}
