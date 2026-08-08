package com.uriel.logpose.core.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.cognitive.CognitivePipeline
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Receiver for injecting tests from CTC (Lab only).
 */
class TrainingReceiver : BroadcastReceiver() {

    private val scope = CoroutineScope(Dispatchers.Default)

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "com.uriel.logpose.INJECT_COGNITIVE_TEST" -> {
                val text = intent.getStringExtra("text") ?: return
                LogPoseLogger.i("🧠 [CTC] Injection Received: '$text'")
                
                com.uriel.logpose.core.services.LogPoseCallService.instance?.let { service ->
                    if (!service.isTripActive) {
                        LogPoseLogger.i("🧠 [CTC] Forcing trip start for testing.")
                        service.startTrip()
                    }
                }

                scope.launch {
                    val forcedIntent = intent.getStringExtra("intent")
                    if (forcedIntent != null) {
                        LogPoseLogger.i("🧠 [CTC] Forcing Intent and Learning: $forcedIntent")
                        val intentEnum = com.thamis.lab.core.contracts.intent.Intent.valueOf(forcedIntent.uppercase())
                        
                        com.uriel.logpose.thamis.learning.LearningEngine.registerCorrection(text, intentEnum)

                        val decision = com.uriel.logpose.thamis.decision.Decision(intentEnum, 1.0f)
                        val command = com.uriel.logpose.thamis.action.ActionMapper.map(decision, text)
                        com.uriel.logpose.thamis.orchestrator.SystemOrchestrator.dispatchCompatCommand(command)
                    } else {
                        CognitivePipeline.process(rawText = text, speechConfidence = 1.0f, noiseLevel = 0.0f)
                    }
                }
            }
            "com.uriel.logpose.SYNC_PSP" -> {
                LogPoseLogger.i("🧠 [CTC] PSP Sync Requested.")
                scope.launch {
                    com.uriel.logpose.thamis.language.PhoneticEngine.syncWithLab()
                }
            }
            "com.uriel.logpose.TRIGGER_EVOLUTION" -> {
                LogPoseLogger.i("🧠 [EVOLUTION] Triggering Autonomous Cycle.")
                com.uriel.logpose.thamis.evolution.EvolutionEngine.triggerEvolutionCycle()
            }
            "com.uriel.logpose.INJECT_ANOMALY" -> {
                val domain = intent.getStringExtra("domain") ?: "Debug"
                val latency = intent.getLongExtra("latency", 0L)
                LogPoseLogger.i("🧠 [EVOLUTION] Injecting latency anomaly: $latency ms in $domain")
                com.uriel.logpose.thamis.world.audit.WorldAudit.record(
                    com.uriel.logpose.thamis.world.audit.WorldTrace(
                        snapshotId = "debug_id",
                        affectedDomain = domain,
                        description = "Injected Anomaly",
                        latencyMs = latency
                    )
                )
            }
            "com.uriel.logpose.INJECT_BT_ANOMALY" -> {
                val type = intent.getStringExtra("type") ?: "SCO_LATENCY"
                val value = intent.getLongExtra("value", 0L)
                LogPoseLogger.i("🧠 [EVOLUTION] Injecting Bluetooth anomaly: $type = $value")
                
                if (type == "SCO_LATENCY") {
                    com.uriel.logpose.thamis.evolution.BluetoothIntelligence.recordEvent(
                        com.uriel.logpose.thamis.evolution.BluetoothEventType.SCO_CONNECTED,
                        "DEBUG_LATENCY_$value",
                        forcedDuration = value
                    )
                } else if (type == "DISCONNECT") {
                    com.uriel.logpose.thamis.evolution.BluetoothIntelligence.recordEvent(
                        com.uriel.logpose.thamis.evolution.BluetoothEventType.CONNECTION_LOST
                    )
                }
            }
        }
    }
}
