package com.uriel.logpose.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uriel.logpose.core.app.AppContainer

@Composable
fun SettingsScreen(
    onOpenDrawer: () -> Unit = {}
) {
    val settings = AppContainer.settingsManager
    val state by settings.state.collectAsState()
    val isSystemDark = isSystemInDarkTheme()
    val isDarkMode = settings.getBoolean("dark_mode", isSystemDark)
    
    val text = if (isDarkMode) Color.White else Color.Black
    val variant = Color(0xFFB2BEC3)
    
    var showIpDialog by remember { mutableStateOf(false) }
    var tempIp by remember { mutableStateOf(settings.getString("pc_ip", "192.168.1.33") ?: "192.168.1.33") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDarkMode) Color.Black else Color.White)
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
                text = "CONFIGURACIÓN",
                style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 4.sp),
                color = text
            )
        }

        Spacer(Modifier.height(48.dp))

        // Sección General
        SettingsGroup("ESTILO Y SISTEMA", variant) {
            SettingsToggle("Modo Oscuro", isDarkMode, text) {
                settings.setBoolean("dark_mode", it)
            }
            SettingsToggle("Auto-activar LogPose", settings.getBoolean("auto_start", false), text) {
                settings.setBoolean("auto_start", it)
            }
            SettingsItem(Icons.Default.Language, "Idioma", "Español", text) {
                // Futuro: Selector de idioma
            }
        }

        // Sección Audio
        SettingsGroup("AUDIO DEL ASISTENTE", variant) {
            val vol = settings.getInt("assistant_volume", 80)
            SettingsItem(Icons.AutoMirrored.Filled.VolumeUp, "Volumen", "$vol%", text) {
                // Futuro: Slider de volumen asistente
            }
            SettingsToggle("Confirmaciones sonoras", settings.getBoolean("audio_feedback", true), text) {
                settings.setBoolean("audio_feedback", it)
            }
        }

        // Sección PC
        SettingsGroup("LOGPOSE PC BRIDGE", variant) {
            val pcIp = settings.getString("pc_ip", "192.168.1.33") ?: "192.168.1.33"
            SettingsItem(Icons.Default.Computer, "IP de la PC", pcIp, text) {
                tempIp = pcIp
                showIpDialog = true
            }
        }

        // Diálogo para editar IP
        if (showIpDialog) {
            AlertDialog(
                onDismissRequest = { showIpDialog = false },
                title = { Text("Configurar IP del PC") },
                text = {
                    Column {
                        Text("Ingresa la IP que ves en el monitor de tu PC:")
                        Spacer(Modifier.height(8.dp))
                        TextField(
                            value = tempIp,
                            onValueChange = { tempIp = it },
                            placeholder = { Text("Ej: 192.168.1.33") },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = text,
                                unfocusedTextColor = text
                            )
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        settings.setString("pc_ip", tempIp.trim())
                        showIpDialog = false
                    }) {
                        Text("GUARDAR")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showIpDialog = false }) {
                        Text("CANCELAR")
                    }
                }
            )
        }

        // Sección Seguridad
        SettingsGroup("SEGURIDAD Y PERMISOS", variant) {
            SettingsToggle("Bloqueo en movimiento", settings.getBoolean("lock_on_move", true), text) {
                settings.setBoolean("lock_on_move", it)
            }
            
            val context = androidx.compose.ui.platform.LocalContext.current
            val hasOverlay = android.provider.Settings.canDrawOverlays(context)
            
            SettingsItem(
                icon = Icons.Default.Shield, 
                label = "Permiso de Superposición", 
                value = if (hasOverlay) "Concedido" else "Faltante (Tocar para activar)", 
                textColor = if (hasOverlay) text else Color(0xFFE17055)
            ) {
                if (!hasOverlay) {
                    val intent = android.content.Intent(
                        android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        android.net.Uri.parse("package:${context.packageName}")
                    )
                    context.startActivity(intent)
                }
            }
        }

        // Sección Almacenamiento
        SettingsGroup("MANTENIMIENTO", variant) {
            Text(
                text = "Claves almacenadas: ${state.strings.size + state.booleans.size + state.ints.size}",
                style = MaterialTheme.typography.labelSmall,
                color = variant,
                modifier = Modifier.padding(start = 40.dp)
            )
            SettingsItem(Icons.Default.Delete, "Restablecer todos los ajustes", "Borrar memoria", text) {
                settings.clearAll()
            }
        }
        
        Spacer(Modifier.height(32.dp))
        
        Text(
            text = "LogPose OS - Build 2024.07",
            style = MaterialTheme.typography.labelSmall,
            color = variant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun SettingsGroup(title: String, color: Color, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(bottom = 32.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            color = color
        )
        Spacer(Modifier.height(16.dp))
        content()
    }
}

@Composable
fun SettingsItem(icon: ImageVector, label: String, value: String, textColor: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(20.dp), tint = textColor.copy(alpha = 0.6f))
            Spacer(Modifier.width(20.dp))
            Column {
                Text(label, style = MaterialTheme.typography.bodyLarge, color = textColor)
                if (value.isNotEmpty()) {
                    Text(value, style = MaterialTheme.typography.labelSmall, color = Color(0xFFB2BEC3))
                }
            }
            Spacer(Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, null, modifier = Modifier.size(16.dp), tint = Color(0xFFB2BEC3))
        }
    }
}

@Composable
fun SettingsToggle(label: String, checked: Boolean, textColor: Color, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(Modifier.width(40.dp)) 
            Text(label, style = MaterialTheme.typography.bodyLarge, color = textColor)
        }
        Switch(
            checked = checked, 
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = textColor,
                checkedTrackColor = Color(0xFF00CEC9).copy(alpha = 0.5f),
                uncheckedThumbColor = Color(0xFFDFE6E9),
                uncheckedTrackColor = Color(0xFFF1F2F6)
            )
        )
    }
}
