package com.uriel.logpose.features.notifications

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

@Composable
fun MessagesScreen(onOpenDrawer: () -> Unit, isDark: Boolean) {
    val bg = if (isDark) Color.Black else Color.White
    val text = if (isDark) Color.White else Color.Black
    val variant = Color(0xFFB2BEC3)
    val accent = if (isDark) Color(0xFF00CEC9) else Color.Black

    val settings = AppContainer.settingsManager
    val settingsState by settings.state.collectAsState()

    // Estados reales
    val voiceReadingEnabled = settingsState.booleans["notif_voice_reading"] ?: true
    val whatsappEnabled = settingsState.booleans["notif_app_whatsapp"] ?: true
    val telegramEnabled = settingsState.booleans["notif_app_telegram"] ?: true
    val smsEnabled = settingsState.booleans["notif_app_sms"] ?: true

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
                text = "MENSAJES",
                style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 4.sp),
                color = text
            )
        }

        Spacer(Modifier.height(48.dp))

        // Lectura de Notificaciones
        Text(
            text = "LECTURA AUTOMÁTICA",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp),
            color = variant
        )
        
        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Lectura por voz", style = MaterialTheme.typography.bodyLarge, color = text)
                Text("Escuchar mensajes entrantes", style = MaterialTheme.typography.labelSmall, color = variant)
            }
            Switch(
                checked = voiceReadingEnabled, 
                onCheckedChange = { settings.setBoolean("notif_voice_reading", it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = text,
                    checkedTrackColor = accent.copy(alpha = 0.5f),
                    uncheckedThumbColor = Color(0xFFDFE6E9),
                    uncheckedTrackColor = Color(0xFFF1F2F6)
                )
            )
        }

        Spacer(Modifier.height(48.dp))

        // Aplicaciones Permitidas
        Text(
            text = "APLICACIONES PERMITIDAS",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp),
            color = variant
        )
        
        Spacer(Modifier.height(24.dp))

        AppPermissionItem("WhatsApp", whatsappEnabled, isDark, text, accent) {
            settings.setBoolean("notif_app_whatsapp", it)
        }
        AppPermissionItem("Telegram", telegramEnabled, isDark, text, accent) {
            settings.setBoolean("notif_app_telegram", it)
        }
        AppPermissionItem("Mensajes (SMS)", smsEnabled, isDark, text, accent) {
            settings.setBoolean("notif_app_sms", it)
        }

        Spacer(Modifier.weight(1f))

        // Info de Privacidad
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E1E1E) else Color(0xFFF1F2F6))
        ) {
            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, null, tint = variant, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "LogPose solo leerá mensajes cuando el casco esté conectado y no estés hablando.",
                    style = MaterialTheme.typography.bodySmall,
                    color = text.copy(alpha = 0.6f)
                )
            }
        }
        
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun AppPermissionItem(
    name: String, 
    enabled: Boolean, 
    isDark: Boolean, 
    textColor: Color, 
    accent: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        onClick = { onCheckedChange(!enabled) },
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(if (isDark) Color(0xFF1E1E1E) else Color(0xFFF1F2F6), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (enabled) Icons.Default.Check else Icons.Default.Close, 
                    null, 
                    modifier = Modifier.size(18.dp),
                    tint = if (enabled) (if (isDark) accent else Color.Black) else Color(0xFFD63031)
                )
            }
            Spacer(Modifier.width(20.dp))
            Text(name, style = MaterialTheme.typography.bodyLarge, color = textColor)
            Spacer(Modifier.weight(1f))
            Checkbox(
                checked = enabled, 
                onCheckedChange = { onCheckedChange(it) },
                colors = CheckboxDefaults.colors(
                    checkedColor = if (isDark) accent else Color.Black,
                    uncheckedColor = textColor.copy(alpha = 0.3f)
                )
            )
        }
    }
}
