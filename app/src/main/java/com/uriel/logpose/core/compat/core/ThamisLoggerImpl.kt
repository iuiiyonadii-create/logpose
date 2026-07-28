package com.uriel.logpose.core.compat.core

import com.uriel.logpose.thamis.cognitive.utils.ThamisLogger

/**
 * Implementación Android de la interfaz de logging de THAMIS.
 * Redirige los logs del cerebro al sistema de Caja Negra de LogPose.
 */
class ThamisLoggerImpl : ThamisLogger {
    override fun d(tag: String, message: String) {
        LogPoseLogger.d("[$tag] $message")
    }

    override fun i(tag: String, message: String) {
        LogPoseLogger.i("[$tag] $message")
    }

    override fun w(tag: String, message: String) {
        LogPoseLogger.w("[$tag] $message")
    }

    override fun e(tag: String, message: String, throwable: Throwable?) {
        LogPoseLogger.e("[$tag] $message - ${throwable?.message}")
    }
}
