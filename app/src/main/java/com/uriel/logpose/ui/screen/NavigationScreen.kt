package com.uriel.logpose.features.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.uriel.logpose.features.settings.SettingsViewModel

@Composable
fun NavigationScreen(
    onOpenDrawer: () -> Unit, 
    isDark: Boolean,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val bg = if (isDark) Color.Black else Color.White
    val text = if (isDark) Color.White else Color.Black
    val variant = Color(0xFFB2BEC3)
    val accent = if (isDark) Color(0xFF00CEC9) else Color.Black
    val cardBg = if (isDark) Color(0xFF1E1E1E) else Color(0xFFF1F2F6)

    val settings = viewModel.settingsManager
    val settingsState by viewModel.state.collectAsState()

    // Ajustes reales
    val voiceEnabled = settingsState.booleans["nav_voice_instructions"] ?: true
    val avoidTolls = settingsState.booleans["nav_avoid_tolls"] ?: false
    val autoNight = settingsState.booleans["nav_auto_night"] ?: true
    val lastDestination by NavigationManager.currentDestination.collectAsState()

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
                text = "NAVEGACIÓN",
                style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 4.sp),
                color = text
            )
        }

        Spacer(Modifier.height(48.dp))

        // Destino Actual
        Text(
            text = "DESTINO SELECCIONADO",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            color = variant
        )
        
        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Place, null, tint = accent)
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(lastDestination, style = MaterialTheme.typography.bodyLarge, color = text)
                    Text("Usa la voz para navegar", style = MaterialTheme.typography.labelSmall, color = variant)
                }
            }
        }

        Spacer(Modifier.height(48.dp))

        // Selección de Navegador
        Text(
            text = "NAVEGADOR PREDETERMINADO",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            color = variant
        )
        
        Spacer(Modifier.height(16.dp))

        val currentNav = settingsState.strings["nav_default_app"] ?: "google_maps"
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = currentNav == "google_maps",
                onClick = { settings.setString("nav_default_app", "google_maps") },
                label = { Text("Google Maps") },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = currentNav == "waze",
                onClick = { settings.setString("nav_default_app", "waze") },
                label = { Text("Waze") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(32.dp))

        // Ajustes de Ruta
        Text(
            text = "AJUSTES DE RUTA",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            color = variant
        )

        Spacer(Modifier.height(16.dp))

        NavToggleItem("Instrucciones por voz", voiceEnabled, text, accent) {
            settings.setBoolean("nav_voice_instructions", it)
        }
        NavToggleItem("Evitar peajes", avoidTolls, text, accent) {
            settings.setBoolean("nav_avoid_tolls", it)
        }
        NavToggleItem("Modo nocturno automático", autoNight, text, accent) {
            settings.setBoolean("nav_auto_night", it)
        }

        Spacer(Modifier.weight(1f))

        // Botón de Búsqueda Rápida
        Button(
            onClick = { 
                // Por ahora usamos un destino fijo para probar el flujo mecánico
                NavigationManager.navigateTo("Obelisco, Buenos Aires")
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isDark) accent else Color.Black,
                contentColor = if (isDark) Color.Black else Color.White
            )
        ) {
            Icon(Icons.Default.Search, null)
            Spacer(Modifier.width(12.dp))
            Text(
                "NAVEGAR A OBELISCO",
                style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.sp)
            )
        }
        
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun NavToggleItem(label: String, checked: Boolean, textColor: Color, accent: Color, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = textColor)
        Switch(
            checked = checked, 
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = textColor,
                checkedTrackColor = accent.copy(alpha = 0.5f),
                uncheckedThumbColor = Color(0xFFDFE6E9),
                uncheckedTrackColor = Color(0xFFF1F2F6)
            )
        )
    }
}
