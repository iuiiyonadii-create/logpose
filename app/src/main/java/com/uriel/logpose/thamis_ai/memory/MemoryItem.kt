package com.uriel.logpose.thamis_ai.memory

import java.util.UUID

/**
 * Domain model representing a single record in THAMIS memory.
 */
data class MemoryItem(
    val id: String = UUID.randomUUID().toString(),
    val type: MemoryType,
    val key: String,
    val value: String,
    val createdAt: Long = System.currentTimeMillis(),
    val expiration: Long? = null
)
