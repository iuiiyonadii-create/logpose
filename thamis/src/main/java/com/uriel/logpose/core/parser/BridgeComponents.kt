package com.uriel.logpose.core.parser

import com.uriel.logpose.core.compat.core.LogPoseLogger
import org.json.JSONObject
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

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

class UdpSender(pcIp: String, private val pcPort: Int = 5055) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var socket: DatagramSocket? = null
    private var address: InetAddress? = null
    
    // v58.0: Canal de envío persistente para evitar creación masiva de corrutinas (Misión #058)
    private val sendChannel = Channel<String>(Channel.UNLIMITED)

    init {
        try {
            socket = DatagramSocket().apply {
                soTimeout = 2000
            }
            address = InetAddress.getByName(pcIp)
            
            // Un solo loop de fondo para todos los envíos
            scope.launch {
                for (json in sendChannel) {
                    enviarReal(json)
                }
            }
        } catch (e: Exception) {
            LogPoseLogger.e("UDP", "Fallo al inicializar socket: ${e.message}")
        }
    }
    
    fun enviar(comando: LogPoseCommand) {
        enviarJson(comando.toJson())
    }

    fun enviarJson(json: String) {
        if (!isActive) return
        sendChannel.trySend(json)
    }

    private val isActive: Boolean get() = scope.isActive

    fun close() {
        LogPoseLogger.d("UDP", "Cerrando socket y liberando recursos de red.")
        sendChannel.close()
        scope.cancel()
        try {
            socket?.close()
        } catch (e: Exception) {}
        socket = null
        address = null
    }

    private fun enviarReal(json: String) {
        val s = socket ?: return
        val addr = address ?: return
        
        // Misión #061: Sanitización de Memoria Pre-JNI (Reforzada v75.0)
        if (json.length > 4096) {
            LogPoseLogger.w("UDP", "Payload excedió límite Staff (4KB). Abortando.")
            return 
        }
        val sanitized = json.replace(Regex("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]"), "")

        try {
            val data = sanitized.toByteArray()
            val packet = DatagramPacket(data, data.size, addr, pcPort)
            s.send(packet)
        } catch (ignored: Exception) {
        }
    }
}
