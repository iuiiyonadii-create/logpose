# THAMIS LAB: GLOBAL RESEARCH & BENCHMARK REPORT v1.0

## 1. PROYECTOS ANALIZADOS (BENCHMARK)

### A. Android DeepFilterNet (GitHub)
- **Uso**: Cancelación de ruido basada en redes neuronales.
- **Fortalezas**: Supera al `NoiseSuppressor` nativo de Android en condiciones de viento fuerte (>60km/h).
- **Lección para THAMIS**: No debemos confiar solo en la API nativa de Android para el intercomunicador. Necesitamos un pre-procesamiento JNI.

### B. Oboe Audio (Google)
- **Uso**: Librería C++ para audio de ultra-baja latencia.
- **Fortalezas**: Resuelve el problema de jitter en dispositivos de gama media/baja (Xiaomi Redmi).
- **Lección para THAMIS**: LogPose debe migrar su motor de audio de Java a Oboe para evitar retardos en las alertas críticas de seguridad.

### C. RideSafe Android (GitHub)
- **Uso**: Detección de accidentes mediante IMU.
- **Fortalezas**: Utiliza algoritmos de filtrado para distinguir entre un "frenado brusco" y una "caída".
- **Lección para THAMIS**: Incorporar el modelo de aceleración $A = \sqrt{x^2 + y^2 + z^2}$ con umbral de impacto de 4G para activar alertas de emergencia.

## 2. ESTÁNDARES TÉCNICOS INTEGRADOS

### Clima e Interferencias (ITU-R P.838-3)
La atenuación de la señal por lluvia sigue la ley de potencia $\gamma = k \cdot R^\alpha$. 
- **Lluvia Extrema (100mm/h)**: Provoca una caída de hasta 15dB en señales de alta frecuencia.
- **Simulación THAMIS**: Inyectaremos pérdida de paquetes en el simulador de red cuando el parámetro `RainRate` sea > 30mm/h.

### Vibración Mecánica (ISO 2631-1)
- **Ralentí**: 10-15 Hz.
- **En Ruta**: 63-125 Hz (Rango crítico para fatiga de componentes).
- **Simulación THAMIS**: El motor kinético vibrará a estas frecuencias reales según el estado del motor simulado.

### Bluetooth y Distancia (Log-Distance Path Loss Model)
- **RSSI**: Se calculará como $RSSI = -10n \log_{10}(d) + A$.
- **Cuerpo Humano**: El casco añade una pérdida de -10dB por absorción.

## 3. PROPUESTAS DE EVOLUCIÓN

| Propuesta | Beneficio | Complejidad | Prioridad |
| :--- | :--- | :--- | :--- |
| **Inyección de Ruido MS-SNSD** | Probar STT con ruido real de tráfico de Microsoft. | Media | ALTA |
| **Replay Engine (GPX)** | Reproducir rutas reales grabadas por repartidores. | Media | ALTA |
| **Digital Twin Sync** | El celular virtual replica exactamente la UI del físico. | ALTA | MEDIA |
| **Oboe Audio Migration** | Latencia cero en alertas. | ALTA | MEDIA |

## 4. CONCLUSIÓN Y ROADMAP
THAMIS LAB dejará de ser una herramienta visual para ser un **Software-in-the-Loop**. El próximo Sprint se centrará en la integración de datos de sensores crudos (Raw Sensor Data) inyectados vía ADB.
