package com.uriel.logpose.thamis.context

/**
 * Representación inmutable del estado del conductor y del sistema en un instante T.
 */
data class ContextSnapshot(
    val timestamp: Long = System.currentTimeMillis(),
    val speedKmh: Float,
    val gpsActive: Boolean,
    val isPlayingMusic: Boolean,
    val isPhoneCallActive: Boolean,
    val bluetoothConnected: Boolean,
    val currentFocus: FocusDomain,
    val conversationState: ConversationState,
    val batteryLevel: Int,
    val noiseLevelDb: Float,
    val lastIntent: String?,
    val activeDomain: String?
)

/**
 * El contexto vivo de la sesión actual.
 */
data class SessionContext(
    val startTime: Long = System.currentTimeMillis(),
    var currentFocus: FocusDomain = FocusDomain.NONE,
    var conversationState: ConversationState = ConversationState.IDLE,
    var lastCommand: String? = null,
    var lastIntention: String? = null,
    var lastDestination: String? = null,
    var lastSongInfo: String? = null,
    var pendingAction: PendingAction? = null,
    val snapshots: MutableList<ContextSnapshot> = mutableListOf()
) {
    fun addSnapshot(snapshot: ContextSnapshot) {
        snapshots.add(snapshot)
        if (snapshots.size > ContextLifetime.SNAPSHOT_LIMIT) {
            snapshots.removeAt(0)
        }
    }

    fun clearExpiredActions() {
        if (pendingAction?.isExpired() == true) {
            pendingAction = null
        }
    }
}
