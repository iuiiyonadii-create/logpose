package com.uriel.logpose.features.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.uriel.logpose.features.voice.VoiceSlotManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceSlotsScreen(
    slotManager: VoiceSlotManager,
    onBack: () -> Unit,
    onTestSlot: (String) -> Unit
) {
    // Usamos un trigger para refrescar la lista cuando cambie un slot
    var refreshTrigger by remember { mutableStateOf(0) }
    val slots = remember(refreshTrigger) { slotManager.getAllSlots() }
    
    var editingSlot by remember { mutableStateOf<Int?>(null) }
    var editName by remember { mutableStateOf("") }
    var editQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🎙️ Atajos de Voz") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("En ruta, decí:", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "\"Número uno\" → reproduce el artista del slot 1\n" +
                        "\"Siguiente / Pausa\" → controles directos",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(slots) { slot ->
                    SlotCard(
                        slotNumber = slot.slotNumber,
                        artistName = slot.artistName,
                        spotifyQuery = slot.spotifyQuery,
                        onEdit = {
                            editingSlot = slot.slotNumber
                            editName = slot.artistName
                            editQuery = slot.spotifyQuery
                        },
                        onTest = { onTestSlot(slot.spotifyQuery) },
                        onDelete = {
                            slotManager.setSlot(slot.slotNumber, "", "")
                            refreshTrigger++
                        }
                    )
                }
            }

            OutlinedButton(
                onClick = {
                    slotManager.setDefaults()
                    refreshTrigger++
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("↺ Restaurar defaults")
            }
        }
    }

    if (editingSlot != null) {
        AlertDialog(
            onDismissRequest = { editingSlot = null },
            title = { Text("Editar Slot $editingSlot") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Nombre visible") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editQuery,
                        onValueChange = { editQuery = it },
                        label = { Text("Búsqueda Spotify") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        slotManager.setSlot(editingSlot!!, editName, editQuery)
                        editingSlot = null
                        refreshTrigger++
                    }
                ) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { editingSlot = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun SlotCard(
    slotNumber: Int,
    artistName: String,
    spotifyQuery: String,
    onEdit: () -> Unit,
    onTest: () -> Unit,
    onDelete: () -> Unit
) {
    val isEmpty = artistName.isBlank()
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isEmpty) MaterialTheme.colorScheme.surfaceVariant 
                             else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = slotNumber.toString(), color = MaterialTheme.colorScheme.onPrimary)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = if (isEmpty) "Vacío" else artistName, style = MaterialTheme.typography.titleMedium)
                if (!isEmpty) Text(text = "Query: $spotifyQuery", style = MaterialTheme.typography.bodySmall)
            }
            if (!isEmpty) {
                IconButton(onClick = onTest) { Icon(Icons.Default.PlayArrow, "Test", tint = MaterialTheme.colorScheme.primary) }
            }
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, "Edit") }
            if (!isEmpty) {
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error) }
            }
        }
    }
}
