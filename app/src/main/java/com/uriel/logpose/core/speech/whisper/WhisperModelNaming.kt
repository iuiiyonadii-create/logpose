package com.uriel.logpose.core.speech.whisper

import java.io.File

/**
 * Convención centralizada de nombres de archivo soportados para el motor Whisper (Sherpa-ONNX).
 */
object WhisperModelNaming {
    val ENCODER_CANDIDATES = listOf(
        "tiny.en-encoder.int8.onnx",
        "tiny-encoder.int8.onnx",
        "encoder.onnx"
    )

    val DECODER_CANDIDATES = listOf(
        "tiny.en-decoder.int8.onnx",
        "tiny-decoder.int8.onnx",
        "decoder.onnx"
    )

    val TOKENS_CANDIDATES = listOf(
        "tiny.en-tokens.txt",
        "tiny-tokens.txt",
        "tokens.txt"
    )

    fun findExistingFile(directory: File, candidates: List<String>): File? {
        return candidates.map { File(directory, it) }.firstOrNull { it.exists() }
    }

    fun hasAnyCandidate(directory: File, candidates: List<String>): Boolean {
        return candidates.any { File(directory, it).exists() }
    }
}
