package com.uriel.logpose.thamis.context

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * FASE 26.9 — LOGPOSE THAMIS CONTEXT ENGINE
 * FASE 7: ACTIVITY DETECTOR
 */
object ActivityDetector {

    enum class Activity { IDLE, RIDING, WALKING, UNKNOWN }

    private var currentActivity = Activity.IDLE

    fun detectActivity(speedKmh: Int) {
        val newActivity = when {
            speedKmh > 15 -> Activity.RIDING
            speedKmh > 2 -> Activity.WALKING
            else -> Activity.IDLE
        }
        
        if (newActivity != currentActivity) {
            currentActivity = newActivity
            LogPoseLogger.i("ActivityDetector: Cambio de actividad detectado: $currentActivity")
        }
    }

    fun getActivity(): Activity = currentActivity
}
