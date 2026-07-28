package com.uriel.logpose.core.audio

import android.content.Context
import android.util.Log

/**
 * Higher-level manager for prioritizing audio events (Navigation > Voice > Music).
 */
class AudioFocusManager(private val context: Context) {

    private val controller = AudioManagerController(context)

    fun requestFocusForVoice(): Boolean {
        Log.d("AudioFocus", "Requesting focus for Voice interaction")
        return controller.requestFocus()
    }

    fun releaseFocusAfterVoice() {
        Log.d("AudioFocus", "Releasing focus after Voice interaction")
        controller.releaseFocus()
    }
}
