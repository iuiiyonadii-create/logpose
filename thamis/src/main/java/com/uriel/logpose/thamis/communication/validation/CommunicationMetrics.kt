package com.uriel.logpose.thamis.communication.validation

import com.uriel.logpose.thamis.communication.model.DecisionType

/**
 * KPIs del dominio de comunicación.
 */
object CommunicationMetrics {
    var totalRequests = 0
    var shadowExecutions = 0
    var confirmations = 0
    var rejections = 0
    var waitings = 0
    var averageLatencyMs = 0L

    fun record(decisionType: DecisionType, latency: Long) {
        totalRequests++
        averageLatencyMs = (averageLatencyMs * (totalRequests - 1) + latency) / totalRequests

        when (decisionType) {
            DecisionType.SHADOW_EXECUTE -> shadowExecutions++
            DecisionType.CONFIRM -> confirmations++
            DecisionType.REJECT -> rejections++
            DecisionType.WAIT -> waitings++
            else -> {}
        }
    }

    fun getAccuracy(): Float {
        return if (totalRequests > 0) (shadowExecutions.toFloat() / totalRequests) else 0f
    }
}
