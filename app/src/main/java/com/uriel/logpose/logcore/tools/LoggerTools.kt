package com.uriel.logpose.logcore.tools

import android.util.Log
import com.uriel.logpose.compat.core.LogPoseLogger

object LoggerTools {

    fun d(tag: String, message: String) {
        LogPoseLogger.d("[$tag] $message")
    }

    fun i(tag: String, message: String) {
        LogPoseLogger.i("[$tag] $message")
    }

    fun w(tag: String, message: String, throwable: Throwable? = null) {
        val text = buildString {
            append("[$tag] $message")
            throwable?.let {
                append("\n")
                append(Log.getStackTraceString(it))
            }
        }
        LogPoseLogger.w(text)
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        val text = buildString {
            append("[$tag] $message")
            throwable?.let {
                append("\n")
                append(Log.getStackTraceString(it))
            }
        }
        LogPoseLogger.e(text)
    }
}