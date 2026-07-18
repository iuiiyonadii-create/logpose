package com.uriel.logpose.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

    val viewModel: BluetoothViewModel =
        viewModel(
            factory = BluetoothViewModelFactory()
        )

    val uiState = viewModel.uiState


    LaunchedEffect(Unit) {
        viewModel.refresh()
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {


        Text(
            text = "LogPose",
            style = MaterialTheme.typography.headlineMedium
        )


        Spacer(
            modifier = Modifier.height(20.dp)
        )


        Text(
            text = "Estado: ${LogPoseEngine.getState()}"
        )


        Text(
            text =
                if (uiState.bluetoothEnabled)
                    "Bluetooth: 🟢 Encendido"
                else
                    "Bluetooth: 🔴 Apagado"
        )


        Spacer(
            modifier = Modifier.height(20.dp)
        )


        Button(
            onClick = {

                if (LogPoseEngine.getState().name == "STOPPED") {

                    val device = uiState.selectedDevice

                    if (device != null) {

                        LogPoseEngine.onBluetoothConnected(
                            device
                        )

                    }

                } else {

                    LogPoseEngine.stop()

                }

            }
        ) {


            Text(
                text =
                    if (LogPoseEngine.getState().name == "STOPPED")
                        "Iniciar LogPose"
                    else
                        "Detener LogPose"
            )

        }


        Spacer(
            modifier = Modifier.height(16.dp)
        )


        Button(
            onClick = {

                viewModel.refresh()

                viewModel.startDiscovery()

            }
        ) {

            Text(
                text = "Buscar dispositivos"
            )

        }


        Spacer(
            modifier = Modifier.height(16.dp)
        )


        Button(
            enabled = uiState.selectedDevice != null,
            onClick = {

                viewModel.saveSelectedDevice()

            }
        ) {

            Text(
                text = "Guardar dispositivo"
            )

        }


        Spacer(
            modifier = Modifier.height(24.dp)
        )


        LazyColumn {


            items(
                items = uiState.devices
            ) { device ->


                DeviceRow(
                    device = device,
                    selected =
                        uiState.selectedDevice?.mac == device.mac,
                    onClick = {

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
) {


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick
            )
            .padding(
                vertical = 8.dp
            )
    ) {


        Text(
            text =
                if (selected)
                    "☑ ${device.name}"
                else
                    "☐ ${device.name}"
        )


        Text(
            text = device.mac,
            style = MaterialTheme.typography.bodySmall
        )


    }


}