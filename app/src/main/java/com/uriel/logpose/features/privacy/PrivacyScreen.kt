package com.uriel.logpose.features.privacy

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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

@Composable
fun PrivacyScreen(onOpenDrawer: () -> Unit, isDark: Boolean) {
    val bg = if (isDark) Color.Black else Color.White
    val text = if (isDark) Color.White else Color.Black
    val variant = Color(0xFFB2BEC3)
    val accent = Color(0xFF00CEC9)

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
                text = "PRIVACIDAD",
                style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 4.sp),
                color = text
            )
        }

        Spacer(Modifier.height(48.dp))

        // Modo Privacidad Maestro
        PrivacyToggle("Modo Privacidad", "Silencia el micrófono por completo", false, text, variant, accent) {}

        Spacer(Modifier.height(32.dp))

        // Configuración de Datos
        PrivacyGroup("GESTIÓN DE DATOS", variant) {
            PrivacyItem(Icons.Default.DeleteSweep, "Limpiar historial de voz", text) {}
            PrivacyItem(Icons.Default.History, "Auto-eliminar registros", text)
        }

        Spacer(Modifier.height(32.dp))

        // Seguridad del Sistema
        PrivacyGroup("ESCUDO LOGPOSE", variant) {
            PrivacyToggle("Bloqueo en movimiento", "Evita cambios de ajustes mientras manejas", true, text, variant, accent) {}
            PrivacyItem(Icons.Default.Security, "Permisos del sistema", text) {}
        }

        Spacer(Modifier.weight(1f))

        Text(
            text = "Tus datos de voz se procesan localmente en el dispositivo para garantizar tu seguridad.",
            style = MaterialTheme.typography.labelSmall,
            color = variant,
            modifier = Modifier.padding(vertical = 24.dp)
        )
    }
}

@Composable
fun PrivacyGroup(title: String, color: Color, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
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
fun PrivacyItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, textColor: Color, onClick: () -> Unit = {}) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(20.dp), tint = textColor.copy(alpha = 0.6f))
            Spacer(Modifier.width(20.dp))
            Text(label, style = MaterialTheme.typography.bodyLarge, color = textColor)
        }
    }
}

@Composable
fun PrivacyToggle(label: String, sublabel: String, checked: Boolean, textColor: Color, variantColor: Color, accent: Color, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyLarge, color = textColor)
            Text(sublabel, style = MaterialTheme.typography.labelSmall, color = variantColor)
        }
        Switch(
            checked = checked, 
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = textColor,
                checkedTrackColor = accent.copy(alpha = 0.5f)
            )
        )
    }
}
