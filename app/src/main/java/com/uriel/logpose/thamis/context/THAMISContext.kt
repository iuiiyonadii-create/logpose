package com.uriel.logpose.thamis.context

import com.thamis.lab.core.contracts.intent.Intent

/**
 * Memoria de corto plazo de THAMIS para mantener el hilo de la conversación.
 */
object THAMISContext {

    private var lastIntent: Intent = Intent.UNKNOWN
    private var lastText: String? = null
    private var lastTimestamp: Long = 0
    private const val CONTEXT_EXPIRATION_MS = 30_000 // 30 segundos

    fun update(intent: Intent, text: String? = null) {
        if (intent != Intent.UNKNOWN) {
            lastIntent = intent
            lastText = text
            lastTimestamp = System.currentTimeMillis()
        }
    }

    fun getActiveIntent(): Intent {
        val now = System.currentTimeMillis()
        return if (now - lastTimestamp < CONTEXT_EXPIRATION_MS) {
            lastIntent
        } else {
            Intent.UNKNOWN
        }
    }
    
    fun getLastText(): String? = lastText

    fun clear() {
        lastIntent = Intent.UNKNOWN
        lastText = null
        lastTimestamp = 0
    }
}
