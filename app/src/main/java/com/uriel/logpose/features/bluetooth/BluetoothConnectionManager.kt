package com.uriel.logpose.features.bluetooth


import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext



class BluetoothConnectionManager(
    context: Context
) {



    private val appContext =
        context.applicationContext



    private var connectedDevice: BluetoothDevice? =
        null



    private var a2dpConnected =
        false



    private var headsetConnected =
        false







    @RequiresPermission(
        Manifest.permission.BLUETOOTH_CONNECT
    )
    suspend fun connect(
        device: BluetoothDevice
    ): Boolean =
        withContext(
            Dispatchers.IO
        ) {



            val adapter =
                BluetoothAdapter.getDefaultAdapter()
                    ?: return@withContext false





            connectedDevice =
                device





            Log.d(
                "LOGPOSE_BT",
                "CHECK CONNECTION ${device.name}"
            )






            adapter.getProfileProxy(

                appContext,

                object : BluetoothProfile.ServiceListener {



                    override fun onServiceConnected(
                        profile: Int,
                        proxy: BluetoothProfile
                    ) {



                        val connected =
                            proxy.connectedDevices.any {


                                it.address ==
                                        device.address


                            }





                        when(profile){



                            BluetoothProfile.A2DP -> {


                                a2dpConnected =
                                    connected



                                Log.d(
                                    "LOGPOSE_BT",
                                    "A2DP=$connected"
                                )


                            }





                            BluetoothProfile.HEADSET -> {


                                headsetConnected =
                                    connected



                                Log.d(
                                    "LOGPOSE_BT",
                                    "HEADSET=$connected"
                                )


                            }



                        }



                    }







                    override fun onServiceDisconnected(
                        profile: Int
                    ) {



                        when(profile){



                            BluetoothProfile.A2DP -> {


                                a2dpConnected =
                                    false


                            }



                            BluetoothProfile.HEADSET -> {


                                headsetConnected =
                                    false


                            }



                        }



                    }



                },

                BluetoothProfile.A2DP

            )









            adapter.getProfileProxy(

                appContext,

                object : BluetoothProfile.ServiceListener {



                    override fun onServiceConnected(
                        profile: Int,
                        proxy: BluetoothProfile
                    ) {



                        headsetConnected =
                            proxy.connectedDevices.any {


                                it.address ==
                                        device.address


                            }



                        Log.d(
                            "LOGPOSE_BT",
                            "HEADSET=$headsetConnected"
                        )



                    }







                    override fun onServiceDisconnected(
                        profile: Int
                    ) {



                        headsetConnected =
                            false


                    }



                },

                BluetoothProfile.HEADSET

            )








            // Esperar respuesta de Android Bluetooth
            delay(2000)







            val result =
                a2dpConnected ||
                        headsetConnected







            Log.d(
                "LOGPOSE_BT",
                "FINAL RESULT=$result"
            )





            result



        }









    fun disconnect(){



        connectedDevice =
            null



        a2dpConnected =
            false



        headsetConnected =
            false



    }









    fun isConnected(): Boolean {


        return a2dpConnected ||
                headsetConnected


    }



}