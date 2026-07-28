package com.uriel.logpose.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.uriel.logpose.ui.Screen
import com.uriel.logpose.core.app.AppContainer
import com.uriel.logpose.features.settings.SettingsScreen
import com.uriel.logpose.features.music.MusicScreen
import com.uriel.logpose.features.profile.ProfileScreen
import com.uriel.logpose.features.notifications.MessagesScreen
import com.uriel.logpose.features.voice.CallsScreen
import com.uriel.logpose.features.privacy.PrivacyScreen
import com.uriel.logpose.features.help.HelpScreen
import com.uriel.logpose.features.navigation.NavigationScreen
import com.uriel.logpose.features.voice.VoiceScreen
import com.uriel.logpose.features.dashboard.TripDashboardScreen
import com.uriel.logpose.features.dashboard.VoiceSlotsScreen
import com.uriel.logpose.features.voice.VoiceSlotManager
import com.uriel.logpose.features.dashboard.DashboardViewModel
import com.uriel.logpose.features.dashboard.DashboardViewModelFactory
import com.uriel.logpose.features.dashboard.TripUiState
import com.uriel.logpose.features.dashboard.TripStatus
import com.uriel.logpose.ui.theme.LogPoseTheme
import kotlinx.coroutines.launch
import androidx.activity.compose.BackHandler
import androidx.compose.ui.platform.LocalContext

@Composable
fun MainScreen() {
    val context = LocalContext.current.applicationContext
    val settings = AppContainer.settingsManager
    val settingsState by settings.state.collectAsStateWithLifecycle()
    
    val isSystemDark = isSystemInDarkTheme()
    val isDarkOverride = settingsState.booleans["dark_mode"] ?: isSystemDark
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Dashboard) }

    val dashboardViewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModelFactory(context, AppContainer.telecom)
    )
    val uiState by dashboardViewModel.uiState.collectAsStateWithLifecycle()

    val effectiveScreen = if (uiState.isTripActive) Screen.TripActive else currentScreen

    BackHandler(enabled = effectiveScreen !is Screen.Dashboard) {
        if (uiState.isTripActive) {
            // Dashboard bloqueado durante el viaje
        } else {
            currentScreen = Screen.Dashboard
        }
    }

    LogPoseTheme(darkTheme = isDarkOverride) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = !uiState.isTripActive,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = if (isDarkOverride) Color.Black else Color.White,
                    drawerContentColor = if (isDarkOverride) Color.White else Color(0xFF2D3436),
                    modifier = Modifier.width(300.dp)
                ) {
                    LogPoseDrawerContent(
                        onNavigate = { screen ->
                            currentScreen = screen
                            scope.launch { drawerState.close() }
                        },
                        isDark = isDarkOverride
                    )
                }
            }
        ) {
            Scaffold(
                containerColor = if (isDarkOverride) Color.Black else Color.White
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    when (effectiveScreen) {
                        is Screen.Dashboard -> LogPoseScreen(
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            isDarkTheme = isDarkOverride,
                            onNavigateToMusic = { currentScreen = Screen.Music },
                            onNavigateToBluetooth = { currentScreen = Screen.Profile },
                            onStartTrip = { 
                                dashboardViewModel.startTrip()
                                // No cambiamos currentScreen aquí, el 'if (uiState.isTripActive)' lo hará
                            }
                        )
                        is Screen.TripActive -> TripDashboardScreen(
                            uiState = uiState,
                            onStartTrip = { dashboardViewModel.startTrip() },
                            onEndTrip = { dashboardViewModel.endTrip() },
                            onDismissBanner = { dashboardViewModel.dismissBanner() }
                        )
                        is Screen.Music -> MusicScreen(
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            isDark = isDarkOverride
                        )
                        is Screen.Voice -> VoiceScreen(
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            isDark = isDarkOverride
                        )
                        is Screen.Profile -> ProfileScreen(
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            isDark = isDarkOverride
                        )
                        is Screen.Messages -> MessagesScreen(
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            isDark = isDarkOverride
                        )
                        is Screen.Calls -> CallsScreen(
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            isDark = isDarkOverride
                        )
                        is Screen.Privacy -> PrivacyScreen(
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            isDark = isDarkOverride
                        )
                        is Screen.Navigation -> NavigationScreen(
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            isDark = isDarkOverride
                        )
                        is Screen.Help -> HelpScreen(
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            isDark = isDarkOverride
                        )
                        is Screen.Settings -> SettingsScreen(onOpenDrawer = { scope.launch { drawerState.open() } })
                        is Screen.VoiceSlots -> VoiceSlotsScreen(
                            slotManager = remember { VoiceSlotManager(context) },
                            onBack = { currentScreen = Screen.Dashboard },
                            onTestSlot = { query ->
                                if (query.isNotBlank()) {
                                    com.uriel.logpose.features.music.MusicManager.play(query)
                                }
                            }
                        )
                        else -> PlaceholderScreen(effectiveScreen, isDarkOverride) { scope.launch { drawerState.open() } }
                    }
                }
            }
        }
    }
}

@Composable
fun LogPoseDrawerContent(onNavigate: (Screen) -> Unit, isDark: Boolean) {
    val text = if (isDark) Color.White else Color.Black
    val variant = Color(0xFFB2BEC3)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDark) Color.Black else Color.White)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "LOGPOSE OS",
            style = MaterialTheme.typography.labelLarge.copy(
                letterSpacing = 4.sp,
                fontWeight = FontWeight.ExtraLight
            ),
            color = variant
        )
        
        Spacer(Modifier.height(48.dp))

        DrawerGroup("SISTEMA", variant) {
            DrawerItem(Icons.Default.Home, "Dashboard", text, onClick = { onNavigate(Screen.Dashboard) })
            DrawerItem(Icons.Default.DirectionsBike, "Modo Viaje", text, onClick = { onNavigate(Screen.TripActive) })
            DrawerItem(Icons.Default.Person, "Perfil / Estado", text, onClick = { onNavigate(Screen.Profile) })
        }

        DrawerGroup("MULTIMEDIA", variant) {
            DrawerItem(Icons.Default.MusicNote, "Música", text, onClick = { onNavigate(Screen.Music) })
            DrawerItem(Icons.Default.SettingsVoice, "Voz", text, onClick = { onNavigate(Screen.Voice) })
            DrawerItem(Icons.Default.Star, "Atajos de Voz", text, onClick = { onNavigate(Screen.VoiceSlots) })
        }

        DrawerGroup("COMUNICACIÓN", variant) {
            DrawerItem(Icons.Default.Call, "Llamadas", text, onClick = { onNavigate(Screen.Calls) })
            DrawerItem(Icons.Default.Message, "Mensajes", text, onClick = { onNavigate(Screen.Messages) })
        }

        DrawerGroup("NAVEGACIÓN", variant) {
            DrawerItem(Icons.Default.Navigation, "GPS / Mapas", text, onClick = { onNavigate(Screen.Navigation) })
        }

        DrawerGroup("SEGURIDAD", variant) {
            DrawerItem(Icons.Default.Lock, "Privacidad", text, onClick = { onNavigate(Screen.Privacy) })
        }

        DrawerGroup("AJUSTES", variant) {
            DrawerItem(Icons.Default.Settings, "Configuración", text, onClick = { onNavigate(Screen.Settings) })
            DrawerItem(Icons.Default.Help, "Ayuda", text, onClick = { onNavigate(Screen.Help) })
        }

        Spacer(Modifier.weight(1f))
        Spacer(Modifier.height(24.dp))
        
        Text(
            text = "v4.0.1 - STABLE",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFFDFE6E9),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun DrawerGroup(title: String, color: Color, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(bottom = 32.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp),
            color = color
        )
        Spacer(Modifier.height(16.dp))
        content()
    }
}

@Composable
fun DrawerItem(icon: ImageVector, label: String, color: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                icon, 
                contentDescription = null, 
                modifier = Modifier.size(18.dp),
                tint = color.copy(alpha = 0.7f)
            )
            Spacer(Modifier.width(24.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light),
                color = color
            )
        }
    }
}

@Composable
fun PlaceholderScreen(screen: Screen, isDark: Boolean, onOpenDrawer: () -> Unit) {
    val bg = if (isDark) Color.Black else Color.White
    val text = if (isDark) Color.White else Color.Black
    val variant = Color(0xFFB2BEC3)

    Column(
        modifier = Modifier.fillMaxSize().background(bg).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = onOpenDrawer) {
                Icon(Icons.Default.Menu, "Menú", tint = text)
            }
        }
        
        Spacer(Modifier.weight(1f))
        Text(
            text = screen.javaClass.simpleName.uppercase(),
            style = MaterialTheme.typography.headlineMedium.copy(letterSpacing = 4.sp, fontWeight = FontWeight.ExtraLight),
            color = text
        )
        Text(
            text = "ESTA SECCIÓN ESTÁ EN DESARROLLO",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp),
            color = variant
        )
        Spacer(Modifier.weight(1.2f))
    }
}
