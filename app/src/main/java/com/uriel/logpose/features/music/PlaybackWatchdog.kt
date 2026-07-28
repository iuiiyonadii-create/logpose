package com.uriel.logpose.features.music

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * PlaybackWatchdog: Gestiona los reintentos cuando se detecta un "freeze" en la reproducción.
 * Evita múltiples reintentos simultáneos y mantiene la memoria de qué se estaba intentando reproducir.
 */
class PlaybackWatchdog(
    private val scope: CoroutineScope,
    private val actions: MusicManagerActions,
    private val maxRetries: Int = 3,
    private val baseDelayMs: Long = 1500L
) {
    private var activeJob: Job? = null
    private var lastRequestedEntity: String = ""
    private var retryCount: Int = 0

    /**
     * Registra una nueva solicitud de reproducción.
     */
    fun trackRequest(entity: String) {
        lastRequestedEntity = entity
        retryCount = 0
        activeJob?.cancel()
    }

    /**
     * Se llama cuando el sistema detecta que la música se detuvo inesperadamente.
     */
    fun onFreezeConfirmed() {
        activeJob?.cancel()

        if (retryCount >= maxRetries) {
            actions.onWatchdogGaveUp(lastRequestedEntity)
            return
        }

        retryCount++
        val backoffDelay = baseDelayMs * retryCount

        activeJob = scope.launch {
            delay(backoffDelay)
            if (lastRequestedEntity.isNotBlank()) {
                actions.retryPlay(lastRequestedEntity)
            } else {
                actions.resumeGeneric()
            }
        }
    }

    /**
     * Se llama cuando la reproducción se confirma como exitosa.
     */
    fun onPlaybackConfirmed() {
        activeJob?.cancel()
        activeJob = null
        retryCount = 0
    }
}

interface MusicManagerActions {
    fun retryPlay(entity: String)
    fun resumeGeneric()
    fun onWatchdogGaveUp(lastEntity: String)
}
