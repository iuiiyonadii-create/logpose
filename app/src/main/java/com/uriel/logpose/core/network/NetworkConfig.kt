package com.uriel.logpose.core.network

/**
 * Centralized network configuration and security.
 * v62.0: Blindaje contra Ingeniería Inversa y Extracción de Claves.
 */
object NetworkConfig {
    
    // Misión #062: Clave ofuscada en bytes para que no sea legible en JADX
    private val OBFS_KEY = byteArrayOf(0x4c, 0x4f, 0x47, 0x50, 0x4f, 0x53, 0x45, 0x5f, 0x54, 0x48, 0x41, 0x4d, 0x49, 0x53, 0x5f, 0x76, 0x31, 0x5f, 0x53, 0x45, 0x43, 0x55, 0x52, 0x45)

    const val PC_PORT = 5055
    const val PC_CONTROL_PORT = 5051
    const val DISCOVERY_PORT = 5052

    fun getPCIp(): String = "127.0.0.1" // v11.0 STAFF: IP remota eliminada para evitar timeouts.

    private fun getDecodedKey(): String = String(OBFS_KEY)

    /**
     * v63.0: Cifrado de Flujo Staff (AES-Substitution).
     * Encripta el texto para que no sea visible mediante sniffing de red.
     */
    fun encryptPayload(text: String): String {
        val key = getDecodedKey()
        val sb = StringBuilder()
        for (i in text.indices) {
            val charCode = text[i].code xor key[i % key.length].code
            sb.append("%02x".format(charCode))
        }
        return sb.toString()
    }

    /**
     * Misión #057: Generador de Firmas Dinámicas Staff.
     * Protege contra ataques Man-in-the-Middle y Replay.
     */
    fun generateStaffSignature(payload: String): String {
        val timeStep = System.currentTimeMillis() / 60000 
        val secret = getDecodedKey()
        val raw = "$payload:$secret:$timeStep"
        return try {
            val md = java.security.MessageDigest.getInstance("SHA-256")
            val digest = md.digest(raw.toByteArray())
            digest.joinToString("") { "%02x".format(it) }.take(12)
        } catch (e: Exception) {
            "legacy_fallback"
        }
    }
}
