package com.uriel.logpose.core.parser

import org.json.JSONObject
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * LogPoseCommand, CommandParser and UdpSender for PC bridge control.
 */
data class LogPoseCommand(
    val intent: String,
    val target: String? = null
) {
    fun toJson(): String {
        val obj = JSONObject()
        obj.put("intent", intent)
        if (target != null) obj.put("target", target)
        return obj.toString()
    }
}

class CommandParser(private val glosario: PhoneticDictionary) {
    fun parsear(textoCrudo: String): LogPoseCommand? {
        if (textoCrudo.isBlank()) return null
        val texto = textoCrudo.lowercase().trim()

        if (glosario.matchesAny(texto, "control.activar")) return LogPoseCommand("activar")
        if (glosario.matchesAny(texto, "control.desactivar")) return LogPoseCommand("desactivar")

        if (glosario.matchesAny(texto, "verbos.abrir")) {
            val app = glosario.resolverApp(texto)
            if (app != null) return LogPoseCommand("abrir", app)
        }

        if (glosario.matchesAny(texto, "verbos.reproducir")) {
            var query = glosario.limpiarMuletillas(texto)
            val verbos = listOf("reproduci", "reproducí", "mandale play", "dale play", "play", "pone", "poné", "tira", "tirá", "ponete algo de", "ponete", "pasame", "pasa")
            for (v in verbos) query = query.replace(v, " ")
            query = query.replace(Regex("^\\s*(un|una|de|el|la)\\s+"), "").replace(Regex("\\s+"), " ").trim()
            return LogPoseCommand("reproducir", glosario.corregirTitulo(query))
        }

        if (glosario.matchesAny(texto, "verbos.bucle")) return LogPoseCommand("bucle")
        if (glosario.matchesAny(texto, "verbos.pausar")) return LogPoseCommand("pausar")
        if (glosario.matchesAny(texto, "verbos.volumen_subir")) return LogPoseCommand("volumen_subir")
        if (glosario.matchesAny(texto, "verbos.volumen_bajar")) return LogPoseCommand("volumen_bajar")
        if (glosario.matchesAny(texto, "verbos.mute")) return LogPoseCommand("mute")

        return null
    }
}

class UdpSender(private val pcIp: String, private val pcPort: Int = 5050) {
    private val scope = CoroutineScope(Dispatchers.IO)
    fun enviar(comando: LogPoseCommand) {
        scope.launch {
            try {
                DatagramSocket().use { socket ->
                    val data = comando.toJson().toByteArray()
                    val address = InetAddress.getByName(pcIp)
                    val packet = DatagramPacket(data, data.size, address, pcPort)
                    socket.send(packet)
                }
            } catch (e: Exception) {
                // Log error
            }
        }
    }
}
