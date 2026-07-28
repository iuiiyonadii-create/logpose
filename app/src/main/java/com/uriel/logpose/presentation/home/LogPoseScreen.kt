package com.uriel.logpose.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogPoseScreen(
    viewModel: LogPoseViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val core = com.uriel.logpose.thamis.thamis_final.ThamisCore.getInstance(com.uriel.logpose.core.app.LogPoseApplication.instance)
    val engineState by core.state.collectAsState()
    
    val context = androidx.compose.ui.platform.LocalContext.current
    var showHyperOSDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (com.uriel.logpose.core.utils.HyperOSOnboarding.shouldShowAutostartAviso(context)) {
            showHyperOSDialog = true
        }
    }

    if (showHyperOSDialog) {
        AlertDialog(
            onDismissRequest = { showHyperOSDialog = false },
            title = { Text("Configuración de HyperOS") },
            text = { Text("Para que LogPose funcione correctamente en segundo plano, es necesario activar el 'Inicio Automático' en la configuración de tu dispositivo Xiaomi.") },
            confirmButton = {
                TextButton(onClick = {
                    showHyperOSDialog = false
                    com.uriel.logpose.core.utils.HyperOSOnboarding.openAutostartSettings(context)
                }) {
                    Text("Configurar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showHyperOSDialog = false
                    com.uriel.logpose.core.utils.HyperOSOnboarding.postpone(context)
                }) {
                    Text("Ahora no")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LogPose MVP") },
                actions = {
                    IconButton(onClick = { 
                        com.uriel.logpose.core.compat.core.LogPoseLogger.i("LogPoseScreen: Abrir Ajustes (Pendiente Implementación Navegación)")
                    }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StatusCard(state, engineState)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { viewModel.toggleService() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state.isServiceRunning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(text = if (state.isServiceRunning) "Detener Copiloto" else "Iniciar Copiloto")
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (state.lastNotification != null) {
                NotificationPreview(state.lastNotification!!)
            }
        }
    }
}

@Composable
fun StatusCard(state: LogPoseUiState, engineState: com.uriel.logpose.core.compat.core.AppState) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Estado del Sistema", style = MaterialTheme.typography.titleMedium)
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text(text = "Bluetooth: ${state.bluetoothStatus?.state ?: "Desconectado"}")
            Text(text = "Modo Thamis: $engineState")
        }
    }
}

@Composable
fun NotificationPreview(notification: com.uriel.logpose.domain.notifications.NotificationEvent) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Notifications, contentDescription = null)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = "Última Notificación", style = MaterialTheme.typography.labelSmall)
                Text(text = "${notification.application}: ${notification.title}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
