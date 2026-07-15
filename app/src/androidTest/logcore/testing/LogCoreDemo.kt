package com.uriel.logpose.logcore.testing

import com.uriel.logpose.logcore.LogCore
import com.uriel.logpose.logcore.core.intent.VoiceRequest
import com.uriel.logpose.logcore.orchestrator.ActionEngine
import com.uriel.logpose.logcore.orchestrator.ServiceResolver
import com.uriel.logpose.logcore.orchestrator.validator.ActionValidator
import com.uriel.logpose.logcore.pipeline.VoicePipeline
import com.uriel.logpose.logcore.services.MusicService

object LogCoreDemo {

    fun run() {

        val provider = FakeMusicProvider()

        val musicService = MusicService(provider)

        val resolver = ServiceResolver(musicService)

        val validator = ActionValidator()

        val engine = ActionEngine(
            resolver,
            validator
        )

        val logCore = LogCore(engine)

        val request = VoiceRequest(
            text = "reproducir música",
            confidence = 0.98f
        )

        val action = VoicePipeline.process(request)

        val result = logCore.execute(action)

        println(result)
    }
}