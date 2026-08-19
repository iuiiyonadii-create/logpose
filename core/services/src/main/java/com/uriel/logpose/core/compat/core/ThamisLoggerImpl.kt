package com.uriel.logpose.core.compat.core

import com.thamis.lab.core.common.logging.LabLogger

/**
 * Implementación Android de la interfaz de logging de THAMIS.
 * Redirige los logs del cerebro al sistema de Caja Negra de LogPose.
 */
class ThamisLoggerImpl : LabLogger {
    override fun debug(tag: String, message: String) {
        LogPoseLogger.d("[$tag] $message")
    }

    override fun info(tag: String, message: String) {
        LogPoseLogger.i("[$tag] $message")
    }

    override fun warn(tag: String, message: String, throwable: Throwable?) {
        LogPoseLogger.w("[$tag] $message - ${throwable?.message}")
    }

    override fun error(tag: String, message: String, throwable: Throwable?) {
        LogPoseLogger.e("[$tag] $message - ${throwable?.message}")
    }
}
