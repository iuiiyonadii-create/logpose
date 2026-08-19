package com.uriel.logpose.thamis.world.history

import com.uriel.logpose.thamis.world.model.WorldSnapshot

/**
 * Historial circular de los estados del mundo.
 */
object WorldHistory {
    private const val MAX_HISTORY = 1000
    private val history = mutableListOf<WorldSnapshot>()

    fun add(snapshot: WorldSnapshot) {
        history.add(snapshot)
        if (history.size > MAX_HISTORY) {
            history.removeAt(0)
        }
    }

    fun getLatest(): WorldSnapshot? = history.lastOrNull()
    
    fun getFullHistory(): List<WorldSnapshot> = history.toList()
    
    fun clear() = history.clear()
}

data class WorldTimeline(
    val events: List<String>,
    val decisions: List<String>,
    val risks: List<String>
)
