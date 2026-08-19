package com.uriel.logpose.thamis.dialog.metrics

/**
 * Recolector de KPIs del motor de diálogo.
 */
object ConversationMetrics {
    var totalConversations = 0
    var successfulConversations = 0
    var cancelledConversations = 0
    var timeouts = 0
    var averageTurns = 0f
    private var totalTurns = 0

    fun recordConversation(turns: Int, success: Boolean, cancelled: Boolean, timeout: Boolean) {
        totalConversations++
        totalTurns += turns
        if (success) successfulConversations++
        if (cancelled) cancelledConversations++
        if (timeout) timeouts++
        
        averageTurns = totalTurns.toFloat() / totalConversations
    }
}
