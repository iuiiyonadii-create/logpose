package com.uriel.logpose.core.audio

import android.content.Context
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Higher-level manager for prioritizing audio events (Navigation > Voice > Music).
 */
class AudioFocusManager(private val context: Context) {

    private val controller = AudioManagerController(context)

    fun requestFocusForVoice(): Boolean {
        LogPoseLogger.d("AudioFocus", "Requesting focus for Voice interaction")
        return controller.requestFocus()
    }

    fun releaseFocusAfterVoice() {
        LogPoseLogger.d("AudioFocus", "Releasing focus after Voice interaction")
        controller.releaseFocus()
    }
}
