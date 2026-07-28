package com.uriel.logpose.core.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.uriel.logpose.features.voice.CallManager

class PhoneStateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            val number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
            
            when (state) {
                TelephonyManager.EXTRA_STATE_RINGING -> {
                    CallManager.onIncomingCall(number)
                }
                TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                    CallManager.onCallAnswered()
                }
                TelephonyManager.EXTRA_STATE_IDLE -> {
                    CallManager.onCallEnded()
                }
            }
        }
    }
}
