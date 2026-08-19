package com.uriel.logpose.features.music

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.uriel.logpose.features.settings.SettingsViewModel

@Composable
fun MusicScreen(
    onOpenDrawer: () -> Unit, 
    isDark: Boolean,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val musicState by MusicManager.state.collectAsState()
    val volumeLevel by MusicManager.volume.collectAsState()
    val accentColor = Color(0xFF00CEC9)
    
    val bg = if (isDark) Color.Black else Color.White
    val text = if (isDark) Color.White else Color.Black
    val variant = Color(0xFFB2BEC3)
    val cardBg = if (isDark) Color(0xFF1E1E1E) else Color(0xFFF1F2F6)

    // Observamos los cambios en los ajustes de forma reactiva
    val settingsState by settingsViewModel.state.collectAsState()
    val savedPlayer = settingsState.strings["music_default_player"] ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(24.dp)
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
                text = "MULTIMEDIA",
                style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 4.sp),
                color = text
            )
        }

        Spacer(Modifier.height(48.dp))

        // Selección de Reproductor
        Text(
            text = "REPRODUCTOR PREDETERMINADO",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            color = variant
        )
        
        Spacer(Modifier.height(24.dp))

        PlayerRow("Spotify", "com.spotify.music", Icons.Default.MusicNote, true, text, accentColor)

        Spacer(Modifier.height(48.dp))

        // Control de Volumen
        Text(
            text = "NIVEL DE AUDIO",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            color = variant
        )
        
        Spacer(Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { MusicManager.volumeDown() }) {
                Icon(Icons.Default.Remove, null, tint = text)
            }
            
            // Barra de progreso real
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .padding(horizontal = 16.dp)
                    .background(variant.copy(alpha = 0.2f), RoundedCornerShape(2.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(volumeLevel)
                        .fillMaxHeight()
                        .background(if (isDark) accentColor else text, RoundedCornerShape(2.dp))
                )
            }

            IconButton(onClick = { MusicManager.volumeUp() }) {
                Icon(Icons.Default.Add, null, tint = text)
            }
        }

        Spacer(Modifier.weight(1f))

        // Estado de Reproducción e interacción
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            onClick = {
                if (musicState == MusicState.MUSIC_PLAYING) MusicManager.pause()
                else MusicManager.play("")
            }
        ) {
            Row(
                modifier = Modifier.padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(if (isDark) Color.Black else Color.White, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (musicState == MusicState.MUSIC_PLAYING) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = if (musicState == MusicState.MUSIC_PLAYING && isDark) accentColor else text
                    )
                }
                Spacer(Modifier.width(20.dp))
                Column {
                    Text(
                        text = if (musicState == MusicState.MUSIC_PLAYING) "REPRODUCIENDO" else "EN ESPERA",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                        color = variant
                    )
                    Text(
                        text = "Spotify",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = text
                    )
                }
            }
        }
        
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun PlayerRow(name: String, pkg: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isSelected: Boolean, textColor: Color, accent: Color) {
    Surface(
        onClick = { MusicManager.setDefaultPlayer(pkg) },
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                icon, 
                null, 
                modifier = Modifier.size(20.dp), 
                tint = if (isSelected) (if (textColor == Color.Black) Color.Black else accent) else textColor.copy(alpha = 0.4f)
            )
            Spacer(Modifier.width(20.dp))
            Text(
                name, 
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ), 
                color = textColor
            )
            Spacer(Modifier.weight(1f))
            if (isSelected) {
                Icon(Icons.Default.Check, null, modifier = Modifier.size(20.dp), tint = if (textColor == Color.Black) Color.Black else accent)
            }
        }
    }
}
