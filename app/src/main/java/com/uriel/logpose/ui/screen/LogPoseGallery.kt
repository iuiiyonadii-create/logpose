package com.uriel.logpose.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uriel.logpose.core.compat.core.AppState
import com.uriel.logpose.domain.models.DeviceType
import com.uriel.logpose.domain.models.LogPoseDevice
import com.uriel.logpose.ui.theme.LogPoseTheme
import com.uriel.logpose.ui.viewmodel.BluetoothUiState

@Preview(showBackground = true, name = "1. Tactical Night (Contraste)")
@Composable
fun PreviewTactical() {
    MaterialTheme(colorScheme = darkColorScheme(surface = Color(0xFF0A0A0A))) {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0A0A0A))) {
            LogPoseScreenContent(
                uiState = demoState(),
                engineState = AppState.LISTENING,
                allPermissionsGranted = true,
                isPrivacyMode = false,
                isDark = true
            )
        }
    }
}

@Preview(showBackground = true, name = "2. Info Focused (Música)")
@Composable
fun PreviewInfoFocused() {
    LogPoseTheme {
        Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
            LogPoseScreenContent(
                uiState = demoState(),
                engineState = AppState.READY,
                allPermissionsGranted = true,
                isPrivacyMode = false,
                isDark = false
            )
            // Añadimos un extra sutil bajo el orbe para esta versión
            Column(
                modifier = Modifier.fillMaxSize().padding(bottom = 220.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "REPRODUCIENDO: STARBOY - THE WEEKND",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF0984E3)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "3. Telemetry (Más Datos)")
@Composable
fun PreviewTelemetry() {
    LogPoseTheme {
        Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
            LogPoseScreenContent(
                uiState = demoState(),
                engineState = AppState.PROCESSING,
                allPermissionsGranted = true,
                isPrivacyMode = false,
                isDark = false
            )
            // Reemplazamos visualmente la barra de sensores con una doble para esta versión
            Column(modifier = Modifier.fillMaxWidth().padding(top = 110.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    val labelColor = Color.Black.copy(alpha = 0.6f)
                    SensorItem("TEMP", "32°C", labelColor, Color(0xFF2D3436))
                    SensorItem("LAT", "45ms", labelColor, Color(0xFF00CEC9))
                    SensorItem("VOZ", "98%", labelColor, Color(0xFF00CEC9))
                }
            }
        }
    }
}

private fun demoState() = BluetoothUiState(
    serviceRunning = true,
    savedDevice = LogPoseDevice(
        mac = "00:11:22:33:44:55",
        name = "LogPose Alpha",
        type = DeviceType.INTERCOM
    )
)
