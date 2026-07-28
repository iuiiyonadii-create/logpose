package com.uriel.logpose.thamis_ai.memory

/**
 * Classification of memory entries based on duration and purpose.
 */
enum class MemoryType {
    TEMPORARY,  // Short-lived (last conversation, last turn)
    PREFERENCE, // Long-lived (volume, favorite mode)
    HABIT,      // Learned patterns (activates music on connection)
    SYSTEM      // Internal state tracking
}
