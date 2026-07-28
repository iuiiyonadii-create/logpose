package com.uriel.logpose.core.services

import android.telecom.Connection
import android.telecom.ConnectionRequest
import android.telecom.ConnectionService
import android.telecom.PhoneAccountHandle
import android.telecom.DisconnectCause

import com.uriel.logpose.core.app.AppContainer

class LogPoseConnectionService : ConnectionService() {

    override fun onCreateOutgoingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        val connection = LogPoseConnection(applicationContext)
        AppContainer.telecom.setActiveConnection(connection)
        return connection.apply {
            setInitializing()
            setActive()
        }
    }

    override fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        val connection = LogPoseConnection(applicationContext)
        AppContainer.telecom.setActiveConnection(connection)
        return connection.apply {
            setInitializing()
            // Marcamos como silenciosa para evitar ringtone del casco
            setRinging() 
            setActive()
        }
    }

    override fun onCreateOutgoingConnectionFailed(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ) {
        // Log error - typically PhoneAccount not enabled
    }
}
