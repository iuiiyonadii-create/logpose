package com.uriel.logpose.data.music

import com.uriel.logpose.core.music.MusicController
import com.thamis.lab.core.contracts.command.LogPoseCommand
import com.uriel.logpose.domain.repositories.MusicRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepositoryImpl @Inject constructor(
    private val musicController: com.uriel.logpose.core.music.MusicController
) : MusicRepository {

    override fun executeCommand(command: LogPoseCommand): Boolean {
        return musicController.execute(command)
    }

    override fun getVolume(): Int {
        // Implementation for volume retrieval
        return 10 
    }

    override fun setVolume(level: Int) {
        // Implementation for volume setting
    }
}
