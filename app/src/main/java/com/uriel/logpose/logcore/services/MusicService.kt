package com.uriel.logpose.logcore.services

import com.uriel.logpose.logcore.providers.MusicProvider

class MusicService(
    private val provider: MusicProvider
) {

    fun play() =
        provider.play()

    fun pause() =
        provider.pause()

    fun stop() =
        provider.stop()

    fun next() =
        provider.next()

    fun previous() =
        provider.previous()

    fun isPlaying(): Boolean =
        provider.isPlaying()

}