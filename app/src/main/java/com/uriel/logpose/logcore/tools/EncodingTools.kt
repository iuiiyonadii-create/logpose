package com.uriel.logpose.logcore.tools

import java.net.URLDecoder
import java.net.URLEncoder
import java.util.Base64

/**
 * Utilidades de codificación/decodificación de texto y bytes.
 */
object EncodingTools {

    /** Codifica [bytes] en Base64 estándar. */
    fun base64Encode(bytes: ByteArray): String = Base64.getEncoder().encodeToString(bytes)

    /** Codifica [text] (UTF-8) en Base64 estándar. */
    fun base64Encode(text: String): String = base64Encode(text.toByteArray(Charsets.UTF_8))

    /** Decodifica una cadena Base64 estándar a [ByteArray], o `null` si es inválida. */
    fun base64Decode(encoded: String): ByteArray? {
        return try {
            Base64.getDecoder().decode(encoded)
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    /** Decodifica una cadena Base64 estándar a texto UTF-8, o `null` si es inválida. */
    fun base64DecodeToString(encoded: String): String? {
        return base64Decode(encoded)?.toString(Charsets.UTF_8)
    }

    /** Codifica [text] para uso seguro en una URL. */
    fun urlEncode(text: String): String = URLEncoder.encode(text, "UTF-8")

    /** Decodifica un texto previamente codificado para URL. */
    fun urlDecode(text: String): String = URLDecoder.decode(text, "UTF-8")

    /** Convierte [bytes] a su representación hexadecimal en minúsculas. */
    fun toHex(bytes: ByteArray): String = bytes.joinToString("") { "%02x".format(it) }

    /** Convierte una cadena hexadecimal a [ByteArray], o `null` si es inválida. */
    fun fromHex(hex: String): ByteArray? {
        val clean = hex.trim()
        if (clean.length % 2 != 0) return null
        return try {
            ByteArray(clean.length / 2) { i ->
                clean.substring(i * 2, i * 2 + 2).toInt(16).toByte()
            }
        } catch (e: NumberFormatException) {
            null
        }
    }
}
