package com.uriel.logpose.logcore.tools

import java.security.MessageDigest

/**
 * Utilidades de hashing criptográfico (no reversible), basadas en `java.security.MessageDigest`.
 * Para checksums de integridad de archivos, ver [ChecksumTools].
 */
object HashTools {

    /** Calcula el hash MD5 de [text] y lo devuelve en hexadecimal. */
    fun md5(text: String): String = digestHex("MD5", text)

    /** Calcula el hash SHA-1 de [text] y lo devuelve en hexadecimal. */
    fun sha1(text: String): String = digestHex("SHA-1", text)

    /** Calcula el hash SHA-256 de [text] y lo devuelve en hexadecimal. */
    fun sha256(text: String): String = digestHex("SHA-256", text)

    /** Calcula el hash SHA-256 de [bytes] y lo devuelve en hexadecimal. */
    fun sha256(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
        return EncodingTools.toHex(digest)
    }

    /** Indica si [text] produce el hash SHA-256 esperado en [expectedHex]. */
    fun matchesSha256(text: String, expectedHex: String): Boolean {
        return sha256(text).equals(expectedHex, ignoreCase = true)
    }

    private fun digestHex(algorithm: String, text: String): String {
        val digest = MessageDigest.getInstance(algorithm).digest(text.toByteArray(Charsets.UTF_8))
        return EncodingTools.toHex(digest)
    }
}
