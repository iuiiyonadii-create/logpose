package com.uriel.logpose.logprobe.common

import android.util.Log

/**
 * Logging interno de LogProbe (para depurar la propia herramienta).
 * No confundir con ProbeEvent: esto nunca contiene datos capturados
 * de terceros, solo estado interno de LogProbe.
 */
object ProbeLogger {

    private const val TAG = "LogProbe"
    var enabled: Boolean = true

    fun d(message: String) {
        if (enabled) Log.d(TAG, message)
    }

    fun w(message: String) {
        if (enabled) Log.w(TAG, message)
    }

    fun e(message: String, throwable: Throwable? = null) {
        if (enabled) Log.e(TAG, message, throwable)
    }
}
