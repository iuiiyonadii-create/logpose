# Plan de Integración: RNNoise para LogPose

RNNoise es una librería de supresión de ruido basada en redes neuronales recurrentes (RNN) que procesa audio en tiempo real con una latencia extremadamente baja (frames de 10ms).

## 1. Requisitos Técnicos
- Audio: 48kHz, Mono, PCM 16-bit.
- NDK: Requerido para compilar la librería C.
- JNI: Capa de puente entre Kotlin y el código nativo.

## 2. Pasos de Integración (NDK)

1.  **Descargar Fuente:** Clonar [RNNoise](https://github.com/xiph/rnnoise).
2.  **Configurar CMake:** Crear un archivo `CMakeLists.txt` en `app/src/main/cpp` que incluya los archivos fuente de rnnoise (`src/denoise.c`, etc.).
3.  **Crear JNI Wrapper:** Implementar una clase en C++ que inicialice el estado de denoise:
    ```cpp
    DenoiseState *st = rnnoise_create(NULL);
    // Procesar frame
    float out[FRAME_SIZE];
    rnnoise_process_frame(st, out, in);
    ```
4.  **Pipeline en Kotlin:**
    - Crear `NativeNoiseSuppressor.kt`.
    - En `IntercomCaptureManager.kt`, convertir el buffer de 16kHz a 48kHz (o usar 48kHz nativamente si el hardware lo permite).
    - Pasar el buffer por el método nativo antes de entregarlo a Vosk.

## 3. Impacto en Latencia
- Procesamiento: ~10ms por frame.
- Conversión de Sample Rate: ~2-5ms.
- Total sumado: <20ms. 
- **Meta del Proyecto (<200ms):** SE MANTIENE CUMPLIDA.

## 4. Tratamiento por Tipo de Ruido
- **Viento:** El viento satura el micro. El plan es `High-Pass (300Hz) -> RNNoise`.
- **Lluvia:** RNNoise es muy eficaz con ruidos blancos y de alta frecuencia.
- **Motor:** RNNoise reduce el ruido de fondo constante, pero para un motor de alta cilindrada se evaluará sumar un `Notch Filter` en las frecuencias dominantes.
