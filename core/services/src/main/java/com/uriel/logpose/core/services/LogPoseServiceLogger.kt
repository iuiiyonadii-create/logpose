package com.uriel.logpose.core.services

import android.util.Log

object LogPoseServiceLogger {
    private const val TAG = "LogPoseServices"

    fun d(msg: String) = Log.d(TAG, msg)
    fun i(msg: String) = Log.i(TAG, msg)
    fun w(msg: String) = Log.w(TAG, msg)
    fun e(msg: String) = Log.e(TAG, msg)
}
