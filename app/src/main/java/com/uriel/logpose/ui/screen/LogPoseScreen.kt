package com.uriel.logpose.ui.screen

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uriel.logpose.core.compat.core.AppState
import com.uriel.logpose.core.engine.LogPoseEngine
import com.uriel.logpose.domain.models.DeviceType
import com.uriel.logpose.domain.models.LogPoseDevice
import com.uriel.logpose.features.settings.SettingsViewModel
import com.uriel.logpose.ui.theme.LogPoseTheme
import com.uriel.logpose.ui.viewmodel.BluetoothUiState
import com.uriel.logpose.ui.viewmodel.BluetoothViewModel
import com.uriel.logpose.core.compat.PermissionManager

@Composable
fun LogPoseScreen(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    onOpenDrawer: () -> Unit = {},
    onNavigateToMusic: () -> Unit = {},
    onNavigateToBluetooth: () -> Unit = {},
    onStartTrip: () -> Unit = {},
    viewModel: BluetoothViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    
    val uiState by viewModel.state.collectAsState()
    val engineState by LogPoseEngine.state.collectAsState()
    var isPrivacyMode by remember { mutableStateOf(false) }

    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        viewModel.refresh()
    }

    val allPermissionsGranted = remember(uiState.serviceRunning) {
        PermissionManager.requiredPermissions().all {
            androidx.core.content.ContextCompat.checkSelfPermission(context, it) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }

    LaunchedEffect(Unit) {
        if (!allPermissionsGranted) {
            permissionLauncher.launch(PermissionManager.requiredPermissions())
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    // Observamos errores del ViewModel y los mostramos en un Snackbar
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent // Para que use el fondo del Box
    ) { padding ->
        DisposableEffect(Unit) {
        viewModel.refresh()
        viewModel.registerBatteryReceiver(context)
        onDispose {
            viewModel.unregisterBatteryReceiver()
        }
    }

        LogPoseScreenContent(
            modifier = modifier.padding(padding),
            uiState = uiState,
            engineState = if (isPrivacyMode) AppState.STOPPED else engineState,
            allPermissionsGranted = allPermissionsGranted,
            isPrivacyMode = isPrivacyMode,
            isDark = isDarkTheme,
            onTogglePrivacy = { isPrivacyMode = !isPrivacyMode },
            onToggleTheme = {
                settingsViewModel.settingsManager.setBoolean("dark_mode", !isDarkTheme)
            },
            onToggleService = {
                if (uiState.serviceRunning) viewModel.stopLogPose(context)
                else {
                    // Acción Dashboard: Notificar al sistema que inició el viaje
                    onStartTrip()

                    // Si no hay casco guardado, iniciamos en modo debug/simulación
                    if (uiState.savedDevice == null) {
                        viewModel.startLogPoseDebug(context)
                    } else {
                        viewModel.startLogPose(context)
                    }
                }
            },
            onOpenDrawer = onOpenDrawer,
            onNavigateToMusic = onNavigateToMusic,
            onNavigateToBluetooth = onNavigateToBluetooth
        )
    }
}

@Composable
fun LogPoseScreenContent(
    uiState: BluetoothUiState,
    engineState: AppState,
    allPermissionsGranted: Boolean,
    isPrivacyMode: Boolean,
    isDark: Boolean,
    modifier: Modifier = Modifier,
    onToggleService: () -> Unit = {},
    onTogglePrivacy: () -> Unit = {},
    onToggleTheme: () -> Unit = {},
    onOpenDrawer: () -> Unit = {},
    onNavigateToMusic: () -> Unit = {},
    onNavigateToBluetooth: () -> Unit = {}
) {
    val accentColor = Color(0xFF00CEC9) // El Cian Neón
    
    val backgroundColor = if (isDark) Color.Black else Color.White
    val onSurfaceColor = if (isDark) Color.White else Color.Black
    val variantColor = if (isDark) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f)

    Box(modifier = modifier.fillMaxSize().background(backgroundColor)) {
        // Cabecera equilibrada
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onOpenDrawer) {
                Icon(Icons.Default.Menu, "Menú", tint = onSurfaceColor)
            }

            Text(
                text = uiState.selectedDevice?.name?.uppercase() ?: uiState.savedDevice?.name?.uppercase() ?: "SIN DISPOSITIVO",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                color = variantColor
            )

            IconButton(onClick = onToggleTheme) {
                Icon(
                    if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                    "Modo Oscuro",
                    tint = onSurfaceColor
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(64.dp))
            
            // 1. Barra de Sensores
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // BATERÍA (Prioridad: Casco > Celular)
                val batteryLevel = if (uiState.deviceBattery != null && uiState.deviceBattery != -1) 
                                      uiState.deviceBattery 
                                   else uiState.phoneBattery ?: -1
                
                SensorItem(
                    "BATT", 
                    if (batteryLevel != -1) "$batteryLevel%" else "--",
                    variantColor, 
                    if (isDark) accentColor else onSurfaceColor
                )
                SensorItem(
                    "MOTOR", 
                    if (uiState.serviceRunning && allPermissionsGranted) "LIVE" else "IDLE",
                    variantColor,
                    if (uiState.serviceRunning && allPermissionsGranted) {
                        if (isDark) accentColor else onSurfaceColor
                    } else Color(0xFFD63031)
                )
                SensorItem("GPS", "FIX", variantColor, if (isDark) accentColor else onSurfaceColor)
            }

            Spacer(Modifier.weight(1f))

            // 2. Orbe de Estado
            StatusOrb(engineState, onSurfaceColor)
            
            Spacer(Modifier.height(24.dp))
            
            Text(
                text = if (isPrivacyMode) "PRIVACIDAD ACTIVA" else when (engineState) {
                    AppState.LISTENING -> "ESCUCHANDO..."
                    AppState.PROCESSING -> "PENSANDO..."
                    AppState.SPEAKING -> "HABLANDO"
                    AppState.CALL_INCOMING -> "LLAMADA ENTRANTE"
                    AppState.CALL_ACTIVE -> "EN LLAMADA"
                    AppState.NAVIGATION_ACTIVE -> "NAVEGANDO"
                    AppState.READY -> "MOTOR LISTO (MIC OFF)"
                    else -> "ESPERANDO COMANDO"
                },
                style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 2.sp, fontWeight = FontWeight.Light),
                color = if (isPrivacyMode) Color(0xFFD63031) else if (isDark) accentColor else onSurfaceColor
            )

            Spacer(Modifier.weight(1.2f))

            // 3. Botones Rápidos (Manuales)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickAction(Icons.Default.MusicNote, "MÚSICA", onSurfaceColor, onClick = onNavigateToMusic)
                QuickAction(
                    icon = if (isPrivacyMode) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    label = "PRIVACIDAD",
                    onSurfaceColor = onSurfaceColor,
                    active = isPrivacyMode,
                    onClick = onTogglePrivacy
                )
                QuickAction(Icons.Default.Bluetooth, "CASCO", onSurfaceColor, onClick = onNavigateToBluetooth)
            }

            Spacer(Modifier.height(48.dp))

            // 4. Botón Primario
            Button(
                onClick = onToggleService,
                modifier = Modifier.fillMaxWidth().height(64.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (uiState.serviceRunning) Color(0xFFD63031) else onSurfaceColor,
                    contentColor = if (uiState.serviceRunning) Color.White else backgroundColor
                )
            ) {
                Text(
                    // SINCRO CLAUDE: Usamos el label centralizado del Dashboard
                    // (Asumimos que el ViewModel lo expone correctamente)
                    if (uiState.serviceRunning) "DESACTIVAR" else "INICIAR LOGPOSE",
                    style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Bold)
                )
            }
            
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
fun SensorItem(label: String, value: String, labelColor: Color, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = labelColor)
        Text(value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = valueColor)
    }
}

@Composable
fun StatusOrb(state: AppState, onSurfaceColor: Color) {
    val color by animateColorAsState(
        targetValue = when (state) {
            AppState.LISTENING -> Color(0xFF00CEC9)
            AppState.PROCESSING -> Color(0xFF0984E3)
            AppState.SPEAKING -> Color(0xFF6C5CE7)
            AppState.CALL_INCOMING, AppState.CALL_ACTIVE -> Color(0xFFF1C40F) // Dorado para llamadas
            AppState.NAVIGATION_ACTIVE -> Color(0xFF2ECC71) // Verde esmeralda para GPS
            else -> onSurfaceColor.copy(alpha = 0.6f)
        }, label = "color"
    )

    Box(
        modifier = Modifier
            .size(100.dp)
            .background(color.copy(alpha = 0.05f), CircleShape)
            .border(1.dp, color.copy(alpha = 0.1f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.size(70.dp).background(color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when (state) {
                    AppState.LISTENING -> Icons.Default.Mic
                    AppState.SPEAKING -> Icons.Default.GraphicEq
                    AppState.CALL_INCOMING, AppState.CALL_ACTIVE -> Icons.Default.Call
                    AppState.NAVIGATION_ACTIVE -> Icons.Default.Navigation
                    else -> Icons.Default.Circle
                },
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color.White
            )
        }
    }
}

@Composable
fun QuickAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector, 
    label: String, 
    onSurfaceColor: Color,
    active: Boolean = false, 
    onClick: () -> Unit = {}
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            onClick = onClick,
            shape = CircleShape,
            color = if (active) Color(0xFFD63031).copy(alpha = 0.1f) else onSurfaceColor.copy(alpha = 0.05f),
            modifier = Modifier.size(56.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon, 
                    null, 
                    modifier = Modifier.size(24.dp), 
                    tint = if (active) Color(0xFFD63031) else onSurfaceColor
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            label, 
            style = MaterialTheme.typography.labelSmall, 
            color = if (active) Color(0xFFD63031) else onSurfaceColor.copy(alpha = 0.6f)
        )
    }
}

@Preview(showBackground = true, name = "Modo Claro")
@Composable
fun PreviewLight() {
    LogPoseTheme(darkTheme = false) {
        LogPoseScreenContent(
            uiState = demoState(),
            engineState = AppState.READY,
            allPermissionsGranted = true,
            isPrivacyMode = false,
            isDark = false
        )
    }
}

@Preview(
    showBackground = true, 
    name = "Modo Oscuro",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreviewDark() {
    LogPoseTheme(darkTheme = true) {
        LogPoseScreenContent(
            uiState = demoState(),
            engineState = AppState.LISTENING,
            allPermissionsGranted = true,
            isPrivacyMode = false,
            isDark = true
        )
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
