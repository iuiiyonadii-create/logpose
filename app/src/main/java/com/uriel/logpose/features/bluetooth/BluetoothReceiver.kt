package com.uriel.logpose.features.bluetooth


import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.RequiresPermission
import com.uriel.logpose.core.compat.core.DeviceClassifier
import com.uriel.logpose.core.engine.LogPoseEngine
import com.uriel.logpose.domain.models.LogPoseDevice


class BluetoothReceiver(
    private val onDeviceFound: (LogPoseDevice) -> Unit,
    private val onDiscoveryFinished: () -> Unit,
    private val onDeviceConnected: ((LogPoseDevice) -> Unit)? = null,
    private val onDeviceDisconnected: ((LogPoseDevice) -> Unit)? = null
) : BroadcastReceiver() {


    override fun onReceive(
        context: Context?,
        intent: Intent?
    ) {


        val action = intent?.action


        Log.d(
            "LOGPOSE_BT",
            "RECEIVER ACTION = $action"
        )



        when(action) {



            BluetoothDevice.ACTION_FOUND -> {


                Log.d(
                    "LOGPOSE_BT",
                    "DEVICE FOUND EVENT"
                )


                val device =
                    intent.getParcelableExtra<BluetoothDevice>(
                        BluetoothDevice.EXTRA_DEVICE
                    )
                        ?: return



                val logPoseDevice =
                    device.toLogPoseDevice()



                Log.d(
                    "LOGPOSE_BT",
                    "FOUND DEVICE: ${logPoseDevice.name} (${logPoseDevice.mac})"
                )



                onDeviceFound(
                    logPoseDevice
                )

            }





            BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {


                Log.d(
                    "LOGPOSE_BT",
                    "DISCOVERY STARTED EVENT"
                )


            }





            BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {


                Log.d(
                    "LOGPOSE_BT",
                    "DISCOVERY FINISHED"
                )


                onDiscoveryFinished()

            }





            BluetoothDevice.ACTION_ACL_CONNECTED -> {


                val device =
                    intent.getParcelableExtra<BluetoothDevice>(
                        BluetoothDevice.EXTRA_DEVICE
                    )
                        ?: return



                val logPoseDevice =
                    device.toLogPoseDevice()



                Log.d(
                    "LOGPOSE_BT",
                    "ACL CONNECTED: ${logPoseDevice.name}"
                )



                LogPoseEngine.onBluetoothConnected(
                    logPoseDevice
                )



                onDeviceConnected?.invoke(
                    logPoseDevice
                )


            }





            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {


                val device =
                    intent.getParcelableExtra<BluetoothDevice>(
                        BluetoothDevice.EXTRA_DEVICE
                    )
                        ?: return



                val logPoseDevice =
                    device.toLogPoseDevice()



                Log.d(
                    "LOGPOSE_BT",
                    "ACL DISCONNECTED: ${logPoseDevice.name}"
                )



                LogPoseEngine.onBluetoothDisconnected(
                    logPoseDevice
                )



                onDeviceDisconnected?.invoke(
                    logPoseDevice
                )


            }


        }


    }





    @RequiresPermission(
        Manifest.permission.BLUETOOTH_CONNECT
    )
    private fun BluetoothDevice.toLogPoseDevice(): LogPoseDevice {


        return LogPoseDevice(

            mac = address,


            name =
                name ?: "Desconocido",


            type =
                DeviceClassifier.detect(
                    name.orEmpty()
                ),


            connected = false

        )


    }


}