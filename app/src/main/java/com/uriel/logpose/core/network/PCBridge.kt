package com.uriel.logpose.core.network

/**
 * Bridge interface for PC remote control.
 */
interface PCBridge {
    fun sendCommand(action: String)
    fun startRemoteServer()
    fun stopRemoteServer()
}
