package com.uriel.logpose.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uriel.logpose.core.app.AppContainer
import com.uriel.logpose.core.compat.PermissionManager
import com.uriel.logpose.core.engine.LogPoseEngine
import com.uriel.logpose.ui.viewmodel.BluetoothViewModel
import com.uriel.logpose.ui.viewmodel.BluetoothViewModelFactory

@Composable
fun ProfileScreen(onOpenDrawer: () -> Unit, isDark: Boolean) {
    val context = LocalContext.current
    val backgroundColor = if (isDark) Color.Black else Color.White
    val onSurfaceColor = if (isDark) Color.White else Color.Black
    val variantColor = Color(0xFFB2BEC3)
    val accentColor = if (isDark) Color(0xFF00CEC9) else Color.Black // Negro puro en modo claro
    val cardBg = if (isDark) Color(0xFF1E1E1E) else Color(0xFFF1F2F6)

    val factory = remember { BluetoothViewModelFactory(AppContainer.bluetoothRepository) }
    val viewModel: BluetoothViewModel = viewModel(factory = factory)
    val uiState by viewModel.state.collectAsState()
    val engineState by LogPoseEngine.state.collectAsState()

    var isRunningTest by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Cabecera
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onOpenDrawer) {
                Icon(Icons.Default.Menu, "Menú", tint = onSurfaceColor)
            }
            Spacer(Modifier.width(16.dp))
            Text(
                text = "ESTADO DEL SISTEMA",
                style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 4.sp),
                color = onSurfaceColor
            )
        }

        Spacer(Modifier.height(48.dp))

        // Info del Casco / Dispositivo
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = cardBg
            )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(backgroundColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Bluetooth, 
                        null, 
                        modifier = Modifier.size(28.dp), 
                        tint = if (uiState.selectedDevice != null) (if (isDark) accentColor else onSurfaceColor) else variantColor
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    text = uiState.selectedDevice?.name?.uppercase() ?: "SIN DISPOSITIVO",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = onSurfaceColor,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Text(
                    text = uiState.selectedDevice?.mac ?: "--:--:--:--:--:--",
                    style = MaterialTheme.typography.bodySmall,
                    color = variantColor,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(48.dp))

        // Diagnóstico en tiempo real
        Text(
            text = "INFORME DE TELEMETRÍA",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp),
            color = variantColor
        )
        
        Spacer(Modifier.height(24.dp))

        // Valores Reales
        val hasMicrophone = PermissionManager.hasMicrophonePermission(context)
        val isConnected = uiState.selectedDevice != null
        
        DiagnosticRow(
            "BATERÍA DISPOSITIVO", 
            if (uiState.deviceBattery != null) "${uiState.deviceBattery}%" else "--", 
            if (uiState.deviceBattery != null && isDark) accentColor else if (uiState.deviceBattery != null) onSurfaceColor else variantColor, 
            onSurfaceColor
        )
        
        DiagnosticRow(
            "BATERÍA CELULAR", 
            if (uiState.phoneBattery != null) "${uiState.phoneBattery}%" else "--", 
            if (isDark) accentColor else onSurfaceColor, 
            onSurfaceColor
        )

        DiagnosticRow(
            "HARDWARE MICRO", 
            if (hasMicrophone) "READY" else "PERMISSION REQ", 
            if (hasMicrophone && isDark) accentColor else if (hasMicrophone) onSurfaceColor else Color(0xFFD63031), 
            onSurfaceColor
        )

        DiagnosticRow(
            "MOTOR LOGPOSE", 
            if (uiState.serviceRunning) "ACTIVE" else "IDLE", 
            if (uiState.serviceRunning && isDark) accentColor else if (uiState.serviceRunning) onSurfaceColor else variantColor, 
            onSurfaceColor
        )

        DiagnosticRow(
            "CONEXIÓN AUDIO", 
            if (isConnected) "CONECTADO" else "DISCONNECTED", 
            if (isConnected && isDark) accentColor else if (isConnected) onSurfaceColor else Color(0xFFD63031),
            onSurfaceColor
        )

        Spacer(Modifier.weight(1f))

        // Acción de Mantenimiento
        Button(
            onClick = { isRunningTest = true },
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isDark) accentColor else onSurfaceColor,
                contentColor = if (isDark) Color.Black else Color.White
            )
        ) {
            if (isRunningTest) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = if (isDark) Color.Black else Color.White, strokeWidth = 2.dp)
                Spacer(Modifier.width(16.dp))
                Text("VERIFICANDO...", style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.sp))
            } else {
                Text("EJECUTAR DIAGNÓSTICO", style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.sp))
            }
        }
        
        LaunchedEffect(isRunningTest) {
            if (isRunningTest) {
                kotlinx.coroutines.delay(2000)
                isRunningTest = false
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun DiagnosticRow(label: String, value: String, valueColor: Color, onSurfaceColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = onSurfaceColor.copy(alpha = 0.5f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = valueColor
        )
    }
}
