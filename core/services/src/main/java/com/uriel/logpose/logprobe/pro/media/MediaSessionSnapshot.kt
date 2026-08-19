package com.uriel.logpose.logprobe.media

// Alias semantico: en el modelo compartido ya existe MediaSnapshot
// (com.uriel.logpose.logprobe.models.MediaSnapshot). Este archivo agrupa
// helpers especificos de sesion, sin duplicar el data class.
import com.uriel.logpose.logprobe.models.MediaSnapshot

object MediaSessionSnapshot {
    fun isPlaying(snapshot: MediaSnapshot): Boolean = snapshot.playbackState == "PLAYING"
}
