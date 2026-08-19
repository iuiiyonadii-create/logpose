package com.uriel.logpose.data.memory

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryDao {
    @Query("SELECT * FROM thamis_memory WHERE type = :type")
    fun getByType(type: String): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM thamis_memory WHERE `key` = :key LIMIT 1")
    suspend fun getByKey(key: String): MemoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(memory: MemoryEntity)

    @Query("DELETE FROM thamis_memory WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM thamis_memory WHERE expiration < :now")
    suspend fun clearExpired(now: Long)
}
