package com.uriel.logpose.features.help

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uriel.logpose.core.app.AppContainer
import com.uriel.logpose.core.compat.PermissionManager
import com.uriel.logpose.core.engine.LogPoseEngine
import com.uriel.logpose.ui.viewmodel.BluetoothViewModel
import com.uriel.logpose.ui.viewmodel.BluetoothViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext

@Composable
fun HelpScreen(onOpenDrawer: () -> Unit, isDark: Boolean) {
    val context = LocalContext.current
    val bg = if (isDark) Color.Black else Color.White
    val text = if (isDark) Color.White else Color.Black
    val variant = Color(0xFFB2BEC3)
    val accent = if (isDark) Color(0xFF00CEC9) else Color.Black // Negro puro en modo claro

    val factory = remember { BluetoothViewModelFactory(AppContainer.bluetoothRepository) }
    val btViewModel: BluetoothViewModel = viewModel(factory = factory)
    val uiState by btViewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Cabecera
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onOpenDrawer) {
                Icon(Icons.Default.Menu, "Menú", tint = text)
            }
            Spacer(Modifier.width(16.dp))
            Text(
                text = "DIAGNÓSTICO",
                style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 4.sp),
                color = text
            )
        }

        Spacer(Modifier.height(48.dp))

        Text(
            text = "ESTADO DE LOS MÓDULOS",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp),
            color = variant
        )

        Spacer(Modifier.height(24.dp))

        // Módulos de Hardware
        SystemModule(
            name = "MICRÓFONO",
            status = if (PermissionManager.hasMicrophonePermission(context)) "ACTIVO" else "SIN PERMISO",
            isOk = PermissionManager.hasMicrophonePermission(context),
            icon = Icons.Default.Mic,
            isDark = isDark,
            accent = if (isDark) accent else text // Si no es oscuro, usar el color de texto (negro)
        )

        SystemModule(
            name = "BLUETOOTH",
            status = if (uiState.bluetoothEnabled) "LISTO" else "APAGADO",
            isOk = uiState.bluetoothEnabled,
            icon = Icons.Default.Bluetooth,
            isDark = isDark,
            accent = if (isDark) accent else text
        )

        SystemModule(
            name = "CONEXIÓN CASCO",
            status = if (uiState.savedDevice != null) "VINCULADO" else "NO DETECTADO",
            isOk = uiState.savedDevice != null,
            icon = Icons.Default.Headset,
            isDark = isDark,
            accent = accent
        )

        SystemModule(
            name = "MOTOR LOGPOSE",
            status = if (uiState.serviceRunning) "EJECUTANDO" else "DETENIDO",
            isOk = uiState.serviceRunning,
            icon = Icons.Default.Memory,
            isDark = isDark,
            accent = accent
        )

        Spacer(Modifier.height(48.dp))

        // Info de Versión
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E1E1E) else Color(0xFFF1F2F6))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("LOGPOSE OS v4.0.1", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = text)
                Text("Build: Stable Release 2024", style = MaterialTheme.typography.labelSmall, color = variant)
                Spacer(Modifier.height(16.dp))
                Text(
                    "Todos los sistemas funcionan localmente para garantizar privacidad y baja latencia.",
                    style = MaterialTheme.typography.bodySmall,
                    color = text.copy(alpha = 0.7f)
                )
            }
        }
        
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun SystemModule(name: String, status: String, isOk: Boolean, icon: androidx.compose.ui.graphics.vector.ImageVector, isDark: Boolean, accent: Color) {
    val text = if (isDark) Color.White else Color.Black
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(if (isDark) Color(0xFF1E1E1E) else Color(0xFFF1F2F6), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, modifier = Modifier.size(20.dp), tint = if (isOk) accent else Color(0xFFD63031))
        }
        
        Spacer(Modifier.width(20.dp))
        
        Column {
            Text(name, style = MaterialTheme.typography.labelSmall, color = Color(0xFFB2BEC3))
            Text(status, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = text)
        }
        
        Spacer(Modifier.weight(1f))
        
        if (isOk) {
            Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(16.dp), tint = accent)
        } else {
            Icon(Icons.Default.Error, null, modifier = Modifier.size(16.dp), tint = Color(0xFFD63031))
        }
    }
}
