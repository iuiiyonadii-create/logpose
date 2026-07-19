package com.uriel.logpose.ui.screen


import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uriel.logpose.core.engine.LogPoseEngine
import com.uriel.logpose.domain.models.LogPoseDevice
import com.uriel.logpose.ui.viewmodel.BluetoothViewModel
import com.uriel.logpose.ui.viewmodel.BluetoothViewModelFactory



@Composable
fun LogPoseScreen(
    modifier: Modifier = Modifier
) {



    val factory =
        remember {
            BluetoothViewModelFactory()
        }




    val viewModel: BluetoothViewModel =
        viewModel(
            factory = factory
        )





    val uiState =
        viewModel.uiState





    val engineState by
    LogPoseEngine.state.collectAsState()







    LaunchedEffect(Unit) {

        viewModel.refresh()

    }









    Column(

        modifier =
            modifier
                .fillMaxSize()
                .padding(24.dp)

    ) {





        Text(

            text = "LogPose",

            style =
                MaterialTheme.typography.headlineMedium

        )







        Spacer(
            modifier = Modifier.height(16.dp)
        )







        Text(

            text =
                "Estado: $engineState"

        )







        Text(

            text =
                if(uiState.bluetoothEnabled)

                    "Bluetooth: 🟢 Encendido"

                else

                    "Bluetooth: 🔴 Apagado"

        )








        Spacer(
            modifier = Modifier.height(12.dp)
        )







        Text(

            text =
                "Bluetooth guardado:\n${
                    uiState.savedDevice?.name
                        ?: "Ninguno"
                }"

        )





        if(uiState.savedDevice != null){


            Text(
                text =
                    uiState.savedDevice!!.mac
            )


        }








        Spacer(
            modifier = Modifier.height(12.dp)
        )








        Text(

            text =
                "Selección actual:\n${
                    uiState.selectedDevice?.name
                        ?: "Ninguno"
                }"

        )








        Spacer(
            modifier = Modifier.height(16.dp)
        )








        Button(

            onClick = {


                if(engineState.name == "STOPPED"){


                    viewModel.startLogPose()


                }
                else{


                    viewModel.stopLogPose()


                }


            }

        ){


            Text(

                text =

                    if(engineState.name == "STOPPED")

                        "Iniciar LogPose"

                    else

                        "Detener LogPose"

            )


        }









        Spacer(
            modifier = Modifier.height(12.dp)
        )









        Button(

            onClick = {

                viewModel.startDiscovery()

            }

        ){


            Text(
                text = "Buscar dispositivos"
            )


        }









        Spacer(
            modifier = Modifier.height(12.dp)
        )








        Button(

            enabled =
                uiState.selectedDevice != null,


            onClick = {

                viewModel.saveSelectedDevice()

            }

        ){


            Text(
                text = "Guardar Bluetooth"
            )


        }









        Spacer(
            modifier = Modifier.height(12.dp)
        )








        Button(

            enabled =
                uiState.selectedDevice != null,


            onClick = {

                viewModel.connectSelectedDevice()

            }

        ){



            Text(

                text =

                    if(viewModel.connected)

                        "Conectado 🟢"

                    else

                        "Verificar conexión"

            )


        }









        Spacer(
            modifier = Modifier.height(20.dp)
        )








        Text(
            text = "Dispositivos encontrados"
        )








        LazyColumn {




            items(

                items =
                    uiState.discoveredDevices

            ){ device ->






                DeviceRow(

                    device = device,


                    selected =
                        uiState.selectedDevice?.mac ==
                                device.mac,



                    onClick = {



                        Log.d(
                            "LOGPOSE_UI",
                            "SELECT ${device.name}"
                        )



                        viewModel.selectDevice(
                            device
                        )



                    }


                )



            }



        }





    }





}









@Composable
private fun DeviceRow(

    device: LogPoseDevice,

    selected: Boolean,

    onClick: () -> Unit

){



    Column(

        modifier =
            Modifier

                .fillMaxWidth()

                .clickable(
                    onClick = onClick
                )

                .padding(
                    vertical = 8.dp
                )


    ){



        Text(

            text =

                if(selected)

                    "☑ ${device.name}"

                else

                    "☐ ${device.name}"


        )





        Text(

            text = device.mac,

            style =
                MaterialTheme.typography.bodySmall

        )



    }



}