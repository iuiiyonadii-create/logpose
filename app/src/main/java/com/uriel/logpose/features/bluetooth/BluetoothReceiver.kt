package com.uriel.logpose.features.bluetooth


import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import com.uriel.logpose.core.compat.core.DeviceClassifier
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


        val action =
            intent?.action



        Log.d(
            "LOGPOSE_BT",
            "RECEIVER ACTION = $action"
        )





        when(action) {



            BluetoothDevice.ACTION_FOUND -> {


                Log.d(
                    "LOGPOSE_BT",
                    "ACTION_FOUND RECEIVED"
                )



                processFoundDevice(
                    intent
                )


            }






            BluetoothDevice.ACTION_NAME_CHANGED -> {


                Log.d(
                    "LOGPOSE_BT",
                    "ACTION_NAME_CHANGED RECEIVED"
                )



                processFoundDevice(
                    intent
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
                    getBluetoothDevice(
                        intent
                    )
                        ?: return





                val logPoseDevice =
                    device.toLogPoseDevice()





                Log.d(
                    "LOGPOSE_BT",
                    "ACL CONNECTED: ${logPoseDevice.name}"
                )

                if (context != null) {
                    com.uriel.logpose.thamis.thamis_final.ThamisCore.getInstance(context).onBluetoothConnected(
                        logPoseDevice
                    )
                }

                onDeviceConnected?.invoke(
                    logPoseDevice
                )


            }








            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {



                val device =
                    getBluetoothDevice(
                        intent
                    )
                        ?: return





                val logPoseDevice =
                    device.toLogPoseDevice()





                Log.d(
                    "LOGPOSE_BT",
                    "ACL DISCONNECTED: ${logPoseDevice.name}"
                )

                if (context != null) {
                    com.uriel.logpose.thamis.thamis_final.ThamisCore.getInstance(context).onBluetoothDisconnected(
                        logPoseDevice
                    )
                }

                onDeviceDisconnected?.invoke(
                    logPoseDevice
                )


            }



        }



    }









    private fun processFoundDevice(
        intent: Intent?
    ) {



        val device =
            getBluetoothDevice(
                intent
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









    private fun getBluetoothDevice(
        intent: Intent?
    ): BluetoothDevice? {


        if(intent == null){

            return null

        }





        return if(
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU
        ){


            intent.getParcelableExtra(
                BluetoothDevice.EXTRA_DEVICE,
                BluetoothDevice::class.java
            )


        }
        else{


            @Suppress("DEPRECATION")
            intent.getParcelableExtra(
                BluetoothDevice.EXTRA_DEVICE
            )


        }


    }









    @RequiresPermission(
        Manifest.permission.BLUETOOTH_CONNECT
    )
    private fun BluetoothDevice.toLogPoseDevice(): LogPoseDevice {



        val deviceName =
            try {

                name

            }
            catch(
                _: Exception
            ){

                null

            }





        return LogPoseDevice(


            mac =
                address,



            name =
                deviceName
                    ?.takeIf {
                        it.isNotBlank()
                    }
                    ?: "Desconocido",




            type =
                DeviceClassifier.detect(
                    deviceName.orEmpty()
                ),




            connected =
                false



        )


    }



}