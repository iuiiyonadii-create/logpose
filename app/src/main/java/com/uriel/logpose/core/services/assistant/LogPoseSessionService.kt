package com.uriel.logpose.core.services.assistant

import android.os.Bundle
import android.service.voice.VoiceInteractionSession
import android.service.voice.VoiceInteractionSessionService

class LogPoseSessionService : VoiceInteractionSessionService() {
    override fun onNewSession(args: Bundle?): VoiceInteractionSession {
        return LogPoseSession(this)
    }
}

class LogPoseSession(context: android.content.Context) : VoiceInteractionSession(context)
