package com.uriel.logpose.core.services

import android.telecom.Call
import android.telecom.InCallService
import android.view.KeyEvent
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.features.voice.CallManager

/**
 * LogPoseInCallService: Permite a THAMIS controlar llamadas activas del sistema.
 * Requiere que la app sea configurada como Asistente Predeterminado o sea habilitada manualmente.
 */
class LogPoseInCallService : InCallService() {

    companion object {
        var activeCall: Call? = null
        var instance: LogPoseInCallService? = null
    }

    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        LogPoseLogger.i("InCall: Nueva llamada detectada.")
        activeCall = call
        instance = this

        val phoneNumber = call.details.handle?.schemeSpecificPart
        CallManager.onIncomingCall(phoneNumber)
    }

    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        LogPoseLogger.i("InCall: Llamada finalizada.")
        if (activeCall == call) {
            activeCall = null
        }
        CallManager.onCallEnded()
    }

    fun answerActiveCall() {
        activeCall?.let {
            it.answer(it.details.videoState)
            LogPoseLogger.i("InCall: Llamada ATENDIDA vía voz.")
            CallManager.onCallAnswered()
        }
    }

    fun disconnectActiveCall() {
        activeCall?.let {
            it.disconnect()
            LogPoseLogger.i("InCall: Llamada FINALIZADA vía voz.")
        }
    }
}
