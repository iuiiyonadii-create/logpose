package com.uriel.logpose.features.bluetooth


import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log



class BluetoothStateReceiver(
    private val onStateChanged: (Boolean) -> Unit
) : BroadcastReceiver() {



    override fun onReceive(
        context: Context?,
        intent: Intent?
    ) {


        if(
            intent?.action ==
            BluetoothAdapter.ACTION_STATE_CHANGED
        ){


            val state =
                intent.getIntExtra(
                    BluetoothAdapter.EXTRA_STATE,
                    -1
                )



            Log.d(
                "LOGPOSE_BT",
                "BLUETOOTH STATE CHANGED = $state"
            )



            val enabled =
                state == BluetoothAdapter.STATE_ON



            onStateChanged(enabled)


        }


    }


}