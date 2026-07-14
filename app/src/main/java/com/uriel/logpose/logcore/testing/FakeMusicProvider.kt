package com.uriel.logpose.logcore.testing

import com.uriel.logpose.logcore.providers.MusicProvider
import com.uriel.logpose.logcore.common.LogCoreLogger

class FakeMusicProvider : MusicProvider {

    private var playing = false

    override fun play() {
        playing = true
        println("▶ Playing music")
    }

    override fun pause() {
        playing = false
        println("⏸ Music paused")
    }

    override fun stop() {
        playing = false
        println("⏹ Music stopped")
    }

    override fun next() {
        println("⏭ Next song")
    }

    override fun previous() {
        println("⏮ Previous song")
    }

    override fun isPlaying(): Boolean {
        return playing
    }
    object LogCoreLogger {
    }

}
