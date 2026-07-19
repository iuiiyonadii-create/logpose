package com.uriel.logpose.features.bluetooth


import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import com.uriel.logpose.data.preferences.DevicePreferences
import com.uriel.logpose.domain.models.LogPoseDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext



class BluetoothRepository(
    context: Context
) {



    private val appContext =
        context.applicationContext




    private val bluetoothManager =
        BluetoothManager(
            appContext
        )




    private val connectionManager =
        BluetoothConnectionManager(
            appContext
        )




    private val devicePreferences =
        DevicePreferences(
            appContext
        )




    private var receiver: BluetoothReceiver? =
        null



    private var discovering =
        false







    @RequiresPermission(
        Manifest.permission.BLUETOOTH_CONNECT
    )
    fun isBluetoothEnabled(): Boolean {

        return bluetoothManager
            .isBluetoothEnabled()

    }







    @RequiresPermission(
        Manifest.permission.BLUETOOTH_CONNECT
    )
    fun getPairedDevices(): List<LogPoseDevice> {


        return bluetoothManager
            .getPairedDevices()


    }








    @RequiresPermission(
        Manifest.permission.BLUETOOTH_CONNECT
    )
    fun getSavedDevice(): LogPoseDevice? {


        val mac =
            devicePreferences
                .getSelectedDevice()
                ?: return null




        return bluetoothManager
            .getPairedDevices()
            .find {


                it.mac == mac


            }


    }








    fun saveSelectedDevice(
        mac: String
    ) {


        val device =
            bluetoothManager
                .getPairedDevices()
                .find {


                    it.mac == mac


                }





        if(device == null){

            Log.d(
                "LOGPOSE_BT",
                "DEVICE NOT FOUND"
            )

            return

        }






        if(
            device.name.contains(
                "APP",
                ignoreCase = true
            )
        ){


            Log.d(
                "LOGPOSE_BT",
                "IGNORED APP DEVICE ${device.name}"
            )


            return


        }






        devicePreferences
            .saveSelectedDevice(
                mac
            )




        Log.d(
            "LOGPOSE_BT",
            "DEVICE SAVED ${device.name}"
        )



    }








    fun getSelectedDeviceMac(): String? {


        return devicePreferences
            .getSelectedDevice()


    }









    @RequiresPermission(
        Manifest.permission.BLUETOOTH_SCAN
    )
    fun startDiscovery(
        onDeviceFound: (LogPoseDevice) -> Unit,
        onFinished: () -> Unit
    ) {



        Log.d(
            "LOGPOSE_BT",
            "REPOSITORY START DISCOVERY"
        )





        if(discovering){


            Log.d(
                "LOGPOSE_BT",
                "DISCOVERY ALREADY RUNNING"
            )


            return

        }





        releaseReceiver()





        val adapter =
            BluetoothAdapter
                .getDefaultAdapter()
                ?: return







        receiver =
            BluetoothReceiver(


                onDeviceFound = {

                    onDeviceFound(
                        it
                    )

                },


                onDiscoveryFinished = {


                    discovering =
                        false



                    Log.d(
                        "LOGPOSE_BT",
                        "DISCOVERY FINISHED CALLBACK"
                    )



                    onFinished()



                    releaseReceiver()



                }


            )









        val filter =
            IntentFilter().apply {


                addAction(
                    BluetoothDevice.ACTION_FOUND
                )


                addAction(
                    BluetoothAdapter.ACTION_DISCOVERY_STARTED
                )


                addAction(
                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                )


                addAction(
                    BluetoothDevice.ACTION_ACL_CONNECTED
                )


                addAction(
                    BluetoothDevice.ACTION_ACL_DISCONNECTED
                )


            }








        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){


            appContext.registerReceiver(

                receiver,

                filter,

                Context.RECEIVER_EXPORTED

            )


        }
        else{


            appContext.registerReceiver(

                receiver,

                filter

            )


        }








        discovering =
            true






        val result =
            adapter.startDiscovery()






        Log.d(
            "LOGPOSE_BT",
            "DISCOVERY RESULT = $result"
        )



    }









    @RequiresPermission(
        Manifest.permission.BLUETOOTH_CONNECT
    )
    suspend fun connectDevice(
        device: LogPoseDevice
    ): Boolean =
        withContext(
            Dispatchers.IO
        ) {


            val bluetoothDevice =
                BluetoothAdapter
                    .getDefaultAdapter()
                    ?.getRemoteDevice(
                        device.mac
                    )
                    ?: return@withContext false






            connectionManager
                .connect(
                    bluetoothDevice
                )


        }








    fun disconnectDevice(){


        connectionManager
            .disconnect()


    }








    fun isConnected(): Boolean {


        return connectionManager
            .isConnected()


    }








    fun releaseReceiver(){


        try {


            receiver?.let {


                appContext.unregisterReceiver(
                    it
                )


            }


        }
        catch(
            _: Exception
        ){

        }





        receiver =
            null



    }



}