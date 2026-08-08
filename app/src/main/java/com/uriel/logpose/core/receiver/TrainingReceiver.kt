package com.uriel.logpose.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.uriel.logpose.core.intelligence.NeuroEvolutionSimulator
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * TrainingReceiver: Puente de comunicación entre la PC (ADB) y la IA local.
 */
class TrainingReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action ?: return
        LogPoseLogger.i("Receiver: Recibida señal externa -> $action")
        
        when (action) {
            "com.uriel.logpose.START_TRAINING" -> {
                NeuroEvolutionSimulator.startInfiniteTraining()
            }
            "com.uriel.logpose.STOP_TRAINING" -> {
                NeuroEvolutionSimulator.stopTraining()
            }
        }
    }
}
