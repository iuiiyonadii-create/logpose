package com.uriel.logpose.logprobe.media

import android.media.session.PlaybackState
import android.media.session.MediaController
import com.uriel.logpose.logprobe.common.ProbeClock
import com.uriel.logpose.logprobe.models.MediaSnapshot

object MediaParser {

    fun parse(packageName: String, controller: MediaController?): MediaSnapshot {
        val state = controller?.playbackState?.state
        return MediaSnapshot(
            packageName = packageName,
            playbackState = stateName(state),
            timestampMillis = ProbeClock.nowMillis(),
            hasActiveSession = controller != null
        )
    }

    private fun stateName(state: Int?): String = when (state) {
        PlaybackState.STATE_PLAYING -> "PLAYING"
        PlaybackState.STATE_PAUSED -> "PAUSED"
        PlaybackState.STATE_STOPPED -> "STOPPED"
        PlaybackState.STATE_BUFFERING -> "BUFFERING"
        null -> "UNKNOWN"
        else -> "OTHER($state)"
    }
}
