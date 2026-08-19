package com.uriel.logpose.thamis.feedback.validation

import com.uriel.logpose.thamis.feedback.collector.FeedbackCollector
import com.uriel.logpose.thamis.feedback.model.FeedbackCategory
import com.uriel.logpose.thamis.feedback.model.UserFeedbackEvent
import com.uriel.logpose.thamis.feedback.monitor.FeedbackIntelligenceEngine

/**
 * Suite de simulación para validar el procesamiento masivo de feedback.
 */
class FeedbackStressTest {

    fun runScenario() {
        repeat(1000) { i ->
            FeedbackCollector.record(UserFeedbackEvent(
                userId = "user_$i",
                contextSnapshotId = "snap_$i",
                rawFeedback = "El sistema falló en la curva $i",
                category = if (i % 10 == 0) FeedbackCategory.BUG else FeedbackCategory.UX,
                priority = (10..90).random()
            ))
        }

        FeedbackIntelligenceEngine.processCycle()
    }
}
