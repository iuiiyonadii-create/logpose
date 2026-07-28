package com.uriel.logpose.core.utils

/**
 * Utilidades para limpiar el audio del escape y detectar voz.
 */
object AudioUtils {

    /**
     * Filtro de Banda (Band-Pass): Combina un High-Pass y un Low-Pass.
     * Rango optimizado para voz humana: 300Hz - 3400Hz.
     * Elimina el "hum" del motor y el "silbido" del viento.
     */
    class VoiceBandPassFilter(
        lowCutoffHz: Double = 300.0,
        highCutoffHz: Double = 3400.0,
        sampleRate: Int = 16000
    ) {
        private val hp = HighPassFilter(lowCutoffHz, sampleRate)
        private val lp = LowPassFilter(highCutoffHz, sampleRate)

        fun apply(buffer: ShortArray, length: Int): ShortArray {
            val hped = hp.apply(buffer, length)
            return lp.apply(hped, length)
        }
    }

    class LowPassFilter(cutoffHz: Double, sampleRate: Int) {
        private val rc = 1.0 / (2 * Math.PI * cutoffHz)
        private val dt = 1.0 / sampleRate
        private val alpha = dt / (rc + dt)
        private var prevOutput = 0.0

        fun apply(buffer: ShortArray, length: Int): ShortArray {
            val out = ShortArray(length)
            for (i in 0 until length) {
                val input = buffer[i].toDouble()
                val output = prevOutput + alpha * (input - prevOutput)
                out[i] = output.toInt().coerceIn(-32768, 32767).toShort()
                prevOutput = output
            }
            return out
        }
    }

    /**
     * Filtro pasa-altos: Atenúa frecuencias menores a 300Hz (el rugido del motor).
     */
    class HighPassFilter(cutoffHz: Double = 300.0, sampleRate: Int = 16000) {
        private val rc = 1.0 / (2 * Math.PI * cutoffHz)
        private val dt = 1.0 / sampleRate
        private val alpha = rc / (rc + dt)
        private var prevInput = 0.0
        private var prevOutput = 0.0

        fun apply(buffer: ShortArray, length: Int): ShortArray {
            val out = ShortArray(length)
            for (i in 0 until length) {
                val input = buffer[i].toDouble()
                val output = alpha * (prevOutput + input - prevInput)
                out[i] = output.toInt().coerceIn(-32768, 32767).toShort()
                prevInput = input
                prevOutput = output
            }
            return out
        }
    }

    /**
     * VAD (Voice Activity Detection) mejorado: Energía RMS + Tasa de Cruce por Cero (ZCR).
     */
    class EnergyVad(private var thresholdRms: Double = 200.0) {
        private var noiseFloor = thresholdRms
        private val alpha = 0.98 
        private val maxNoiseRms = 8000.0 

        fun hasVoice(buffer: ShortArray, length: Int): Boolean {
            var sum = 0.0
            var zeroCrossings = 0
            
            for (i in 0 until length) {
                sum += buffer[i].toDouble() * buffer[i].toDouble()
                if (i > 0 && ((buffer[i] >= 0 && buffer[i-1] < 0) || (buffer[i] < 0 && buffer[i-1] >= 0))) {
                    zeroCrossings++
                }
            }
            val rms = Math.sqrt(sum / length)
            val zcr = zeroCrossings.toDouble() / length
            
            // Adaptamos el noiseFloor cuando detectamos "silencio"
            if (rms < noiseFloor * 1.3) {
                noiseFloor = alpha * noiseFloor + (1.0 - alpha) * rms
            }

            // El umbral dinámico se ajusta al ruido ambiente
            val dynamicThreshold = Math.max(thresholdRms, noiseFloor * 1.6)
            
            // La voz humana tiene un ZCR moderado (0.05 - 0.25). 
            // El ruido de viento puro tiene ZCR muy alto (>0.4).
            val isNotPureWind = zcr < 0.35
            
            return rms > dynamicThreshold && isNotPureWind
        }

        /**
         * Retorna el nivel de ruido de fondo normalizado de 0.0 (silencio) a 1.0 (viento extremo).
         */
        fun getNormalizedNoiseLevel(): Float {
            return ((noiseFloor - thresholdRms) / (maxNoiseRms - thresholdRms))
                .coerceIn(0.0, 1.0).toFloat()
        }

        fun reset() {
            noiseFloor = thresholdRms
        }
    }

    /**
     * RingBuffer para ShortArray: Permite guardar audio reciente para re-procesar
     * con motores de alta fidelidad sin latencia.
     */
    class AudioRingBuffer(val capacitySamples: Int) {
        private val buffer = ShortArray(capacitySamples)
        private var writeIndex = 0
        private var totalWritten = 0L

        fun write(data: ShortArray, length: Int) {
            for (i in 0 until length) {
                buffer[writeIndex] = data[i]
                writeIndex = (writeIndex + 1) % capacitySamples
                totalWritten++
            }
        }

        fun getRecentAudio(samplesNeeded: Int): ShortArray {
            val count = samplesNeeded.coerceAtMost(capacitySamples).coerceAtMost(totalWritten.toInt())
            val result = ShortArray(count)
            var readIndex = (writeIndex - count + capacitySamples) % capacitySamples
            for (i in 0 until count) {
                result[i] = buffer[readIndex]
                readIndex = (readIndex + 1) % capacitySamples
            }
            return result
        }
    }
}
