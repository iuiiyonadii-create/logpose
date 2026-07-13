package com.uriel.logpose.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.uriel.logpose.bluetooth.BluetoothRepository
import com.uriel.logpose.engine.LogPoseEngine
import com.uriel.logpose.model.LogPoseDevice

@Composable
fun LogPoseScreen(
    modifier: Modifier = Modifier
) {

    var bluetoothEnabled by remember {
        mutableStateOf(false)
    }

    var devices by remember {
        mutableStateOf<List<LogPoseDevice>>(emptyList())
    }

    var selectedDevice by remember {
        mutableStateOf<LogPoseDevice?>(null)
    }

    val context = LocalContext.current

    val bluetoothRepository = remember {
        BluetoothRepository(context)
    }

    LaunchedEffect(Unit) {

        bluetoothEnabled = bluetoothRepository.isBluetoothEnabled()

        devices = bluetoothRepository.getPairedDevices()

        selectedDevice = bluetoothRepository.getSelectedDevice()

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

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Estado: ${LogPoseEngine.getState()}"
        )

        Text(
            text = if (bluetoothEnabled)
                "Bluetooth: 🟢 Encendido"
            else
                "Bluetooth: 🔴 Apagado"
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {

                if (LogPoseEngine.getState().name == "STOPPED") {

                    selectedDevice?.let {
                        LogPoseEngine.onBluetoothConnected(it)
                    }

                } else {

                    LogPoseEngine.stop()

                }

            }
        ) {

            Text(
                text = if (LogPoseEngine.getState().name == "STOPPED")
                    "Iniciar LogPose"
                else
                    "Detener LogPose"
            )

        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {

                bluetoothEnabled = bluetoothRepository.isBluetoothEnabled()

                devices = bluetoothRepository.getPairedDevices()

                selectedDevice = bluetoothRepository.getSelectedDevice()

            }
        ) {

            Text("Buscar dispositivos")

        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            enabled = selectedDevice != null,
            onClick = {

                selectedDevice?.let {

                    bluetoothRepository.saveSelectedDevice(it.mac)

                }

            }
        ) {

            Text("Guardar dispositivo")

        }

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn {

            items(devices) { device ->

                val selected = selectedDevice?.mac == device.mac

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {

                            selectedDevice = device

                        }
                        .padding(vertical = 8.dp)
                ) {

                    Text(
                        text = if (selected)
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

        }

    }

}