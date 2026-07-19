package com.uriel.logpose.ui.viewmodel


import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uriel.logpose.core.app.AppContainer
import com.uriel.logpose.core.engine.LogPoseEngine
import com.uriel.logpose.domain.models.LogPoseDevice
import kotlinx.coroutines.launch



class BluetoothViewModel : ViewModel() {



    private val repository =
        AppContainer.bluetoothRepository






    var uiState by mutableStateOf(
        BluetoothUiState()
    )
        private set






    var connected by mutableStateOf(false)
        private set







    fun refresh(){


        viewModelScope.launch {



            try {



                val enabled =
                    repository.isBluetoothEnabled()





                val devices =
                    repository.getPairedDevices()





                val saved =
                    repository.getSavedDevice()






                uiState =
                    uiState.copy(


                        bluetoothEnabled =
                            enabled,



                        devices =
                            devices,



                        savedDevice =
                            saved,



                        selectedDevice =
                            saved,



                        loading =
                            false



                    )





                Log.d(
                    "LOGPOSE_UI",
                    "SAVED DEVICE = ${saved?.name}"
                )




            }
            catch(
                exception: Exception
            ){



                uiState =
                    uiState.copy(

                        error =
                            exception.message,


                        loading =
                            false

                    )



            }



        }



    }









    fun startDiscovery(){



        if(uiState.discovering){



            Log.d(
                "LOGPOSE_BT",
                "DISCOVERY ALREADY RUNNING"
            )



            return


        }








        uiState =
            uiState.copy(

                discovering = true,

                discoveredDevices = emptyList()

            )








        repository.startDiscovery(



            onDeviceFound = { device ->





                if(
                    uiState.discoveredDevices.none {

                        it.mac == device.mac

                    }
                ){



                    uiState =
                        uiState.copy(

                            discoveredDevices =
                                uiState.discoveredDevices + device

                        )



                }



            },





            onFinished = {



                uiState =
                    uiState.copy(

                        discovering = false

                    )



                Log.d(
                    "LOGPOSE_UI",
                    "DISCOVERY FINISHED"
                )



            }



        )



    }









    fun selectDevice(
        device: LogPoseDevice
    ){



        uiState =
            uiState.copy(

                selectedDevice =
                    device

            )



        Log.d(
            "LOGPOSE_UI",
            "SELECT ${device.name}"
        )



    }









    fun saveSelectedDevice(){



        val device =
            uiState.selectedDevice
                ?: return






        repository.saveSelectedDevice(
            device.mac
        )






        uiState =
            uiState.copy(

                savedDevice =
                    device

            )






        Log.d(
            "LOGPOSE_UI",
            "SAVED ${device.name}"
        )



    }









    fun connectSelectedDevice(){



        val device =
            uiState.selectedDevice
                ?: return






        viewModelScope.launch {



            val result =
                repository.connectDevice(
                    device
                )






            connected =
                result





            Log.d(
                "LOGPOSE_UI",
                if(result)

                    "DEVICE CONNECTED ${device.name}"

                else

                    "DEVICE NOT CONNECTED ${device.name}"
            )



        }



    }









    fun startLogPose(){



        val device =
            uiState.selectedDevice
                ?: return






        viewModelScope.launch {



            val result =
                repository.connectDevice(
                    device
                )






            if(result){



                LogPoseEngine.onBluetoothConnected(
                    device
                )



                LogPoseEngine.startListening()




                connected =
                    true





                Log.d(
                    "LOGPOSE_UI",
                    "LOGPOSE STARTED ${device.name}"
                )



            }
            else {



                Log.d(
                    "LOGPOSE_UI",
                    "LOGPOSE CONNECTION FAILED"
                )



            }



        }



    }









    fun stopLogPose(){



        LogPoseEngine.stop()



        connected =
            false





        Log.d(
            "LOGPOSE_UI",
            "LOGPOSE STOPPED"
        )



    }









    fun disconnect(){



        repository.disconnectDevice()



        LogPoseEngine.stop()



        connected =
            false






        uiState =
            uiState.copy(

                selectedDevice = null

            )



    }



}