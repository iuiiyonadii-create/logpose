package com.uriel.logpose.data.memory

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room database entity for THAMIS memory items.
 */
@Entity(tableName = "thamis_memory")
data class MemoryEntity(
    @PrimaryKey val id: String,
    val type: String,
    val key: String,
    val value: String,
    val createdAt: Long,
    val expiration: Long?
)
