package com.uriel.logpose.compat.core

import android.util.Log

object LogPoseLogger {

    fun d(message: String) {
        Log.d(Constants.LOG_TAG, message)
    }

    fun i(message: String) {
        Log.i(Constants.LOG_TAG, message)
    }

    fun w(message: String) {
        Log.w(Constants.LOG_TAG, message)
    }

    fun e(message: String) {
        Log.e(Constants.LOG_TAG, message)
    }
}