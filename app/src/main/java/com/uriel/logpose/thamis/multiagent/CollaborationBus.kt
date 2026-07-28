package com.uriel.logpose.thamis.multiagent

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * FASE 27.2 — THAMIS MULTI-AGENT COLLABORATION ENGINE
 * FASE 12: COLLABORATION BUS
 */
object CollaborationBus {
    private val messages = mutableListOf<String>()

    fun postMessage(from: String, content: String) {
        val msg = "[$from]: $content"
        messages.add(msg)
        LogPoseLogger.d("CollaborationBus: $msg")
    }

    fun getHistory(): List<String> = messages.toList()
}
