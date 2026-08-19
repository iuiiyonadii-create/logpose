package com.uriel.logpose.data.memory

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Persistencia de decisiones y trazas de agentes especializados.
 */
@Entity(tableName = "agent_memory")
data class AgentMemoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val agentName: String,
    val timestamp: Long,
    val task: String,
    val result: String,
    val successful: Boolean
)
