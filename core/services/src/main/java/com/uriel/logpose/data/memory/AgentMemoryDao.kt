package com.uriel.logpose.data.memory

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AgentMemoryDao {
    @Query("SELECT * FROM agent_memory WHERE agentName = :agentName ORDER BY timestamp DESC")
    suspend fun getLogsForAgent(agentName: String): List<AgentMemoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: AgentMemoryEntity)

    @Query("DELETE FROM agent_memory WHERE agentName = :agentName")
    suspend fun clearAgent(agentName: String)

    @Query("SELECT * FROM agent_memory WHERE agentName = :agentName AND task LIKE '%' || :query || '%'")
    suspend fun findSimilar(agentName: String, query: String): List<AgentMemoryEntity>
}
