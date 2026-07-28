package com.uriel.logpose.domain.repositories

import com.uriel.logpose.domain.models.LogPoseCommand

/**
 * Contract for Music and Media operations.
 */
interface MusicRepository {
    fun executeCommand(command: LogPoseCommand): Boolean
    fun getVolume(): Int
    fun setVolume(level: Int)
}
