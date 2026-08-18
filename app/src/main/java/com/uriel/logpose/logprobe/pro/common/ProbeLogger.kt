package com.uriel.logpose.logprobe.common

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Logging interno de LogProbe (para depurar la propia herramienta).
 * No confundir con ProbeEvent: esto nunca contiene datos capturados
 * de terceros, solo estado interno de LogProbe.
 */
object ProbeLogger {

    private const val TAG = "LogProbe"
    var enabled: Boolean = true

    fun d(message: String) {
        if (enabled) LogPoseLogger.d(TAG, message)
    }

    fun w(message: String) {
        if (enabled) LogPoseLogger.w(TAG, message)
    }

    fun e(message: String, throwable: Throwable? = null) {
        if (enabled) LogPoseLogger.e(TAG, if (throwable != null) "$message: ${throwable.message}" else message)
    }
}
