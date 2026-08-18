package com.uriel.logpose.core.utils

/**
 * Utilidades para limpiar el audio del escape y detectar voz.
 */
object AudioUtils {

    /**
     * Filtro de Banda (Band-Pass): Combina un High-Pass y un Low-Pass.
     * Rango optimizado para Bluetooth SCO y voz en moto (Sherlock v5.0 Fix): 150Hz - 4000Hz.
     */
    class VoiceBandPassFilter(
        lowCutoffHz: Double = 150.0,
        highCutoffHz: Double = 4000.0,
        sampleRate: Int = 16000
    ) {
        private val hp = HighPassFilter(lowCutoffHz, sampleRate)
        private val lp = LowPassFilter(highCutoffHz, sampleRate)

        fun applyInPlace(buffer: ShortArray, length: Int) {
            hp.applyInPlace(buffer, length)
            lp.applyInPlace(buffer, length)
        }

        // Mantenido por compatibilidad legacy, pero se recomienda usar applyInPlace
        fun apply(buffer: ShortArray, length: Int): ShortArray {
            val result = buffer.copyOf(length)
            applyInPlace(result, length)
            return result
        }
    }

    class LowPassFilter(cutoffHz: Double, sampleRate: Int) {
        private val rc = 1.0 / (2 * Math.PI * cutoffHz)
        private val dt = 1.0 / sampleRate
        private val alpha = dt / (rc + dt)
        private var prevOutput = 0.0

        fun applyInPlace(buffer: ShortArray, length: Int) {
            for (i in 0 until length) {
                val input = buffer[i].toDouble()
                val output = prevOutput + alpha * (input - prevOutput)
                buffer[i] = output.toInt().coerceIn(-32768, 32767).toShort()
                prevOutput = output
            }
        }

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
     * Filtro pasa-altos: Atenúa frecuencias menores a 150Hz (el rugido grave del motor).
     */
    class HighPassFilter(cutoffHz: Double = 150.0, sampleRate: Int = 16000) {
        private val rc = 1.0 / (2 * Math.PI * cutoffHz)
        private val dt = 1.0 / sampleRate
        private val alpha = rc / (rc + dt)
        private var prevInput = 0.0
        private var prevOutput = 0.0

        fun applyInPlace(buffer: ShortArray, length: Int) {
            for (i in 0 until length) {
                val input = buffer[i].toDouble()
                val output = alpha * (prevOutput + input - prevInput)
                buffer[i] = output.toInt().coerceIn(-32768, 32767).toShort()
                prevInput = input
                prevOutput = output
            }
        }

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
    class EnergyVad(private var thresholdRms: Double = 600.0) {
        private var noiseFloor = thresholdRms
        private val alpha = 0.99 // v71.7: Adaptación más lenta para no "comerse" finales de frase
        private val maxNoiseRms = 8000.0 

        fun hasVoice(buffer: ShortArray, length: Int, multiplier: Float = 1.0f): Boolean {
            var sum = 0L
            var zeroCrossings = 0
            
            for (i in 0 until length) {
                val sample = buffer[i].toLong()
                sum += sample * sample
                if (i > 0 && ((buffer[i] >= 0 && buffer[i-1] < 0) || (buffer[i] < 0 && buffer[i-1] >= 0))) {
                    zeroCrossings++
                }
            }
            val rms = Math.sqrt(sum.toDouble() / length)
            val zcr = zeroCrossings.toDouble() / length
            
            // Adaptamos el noiseFloor cuando detectamos "silencio"
            if (rms < noiseFloor * 1.3) {
                noiseFloor = alpha * noiseFloor + (1.0 - alpha) * rms
            }

            // Sherlock v5.1 Adaptive Moto Fix: Umbral dinámico calibrado para señal normalizada por AGC
            // v71.7: Umbral más permisivo (1.35x) para captar decaimiento de voz al final
            val dynamicThreshold = Math.min(1800.0, Math.max(thresholdRms, noiseFloor * 1.35)) * multiplier
            
            // La voz humana tiene un ZCR moderado (0.05 - 0.25). 
            // El ruido de viento puro tiene ZCR muy alto (>0.4).
            val isNotPureWind = zcr < 0.40 // v71.7: Tolerancia aumentada para cascos abiertos
            
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
