package com.uriel.logpose.data.music

import android.content.Context
import com.uriel.logpose.core.music.MusicController
import com.uriel.logpose.domain.models.LogPoseCommand
import com.uriel.logpose.domain.repositories.MusicRepository
import javax.inject.Inject

class MusicRepositoryImpl @Inject constructor(
    private val context: Context
) : MusicRepository {

    private val musicController = MusicController(context)

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
