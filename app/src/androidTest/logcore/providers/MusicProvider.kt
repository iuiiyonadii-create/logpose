package com.uriel.logpose.logcore.providers

interface MusicProvider {

    fun play()

    fun pause()

    fun stop()

    fun next()

    fun previous()

    fun isPlaying(): Boolean

}