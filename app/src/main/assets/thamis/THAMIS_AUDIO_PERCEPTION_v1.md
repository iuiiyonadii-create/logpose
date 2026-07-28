# THAMIS Audio Perception Layer v1.0

La capa de percepción auditiva es el primer eslabón en la cadena de razonamiento de THAMIS. Su misión es asegurar que el "cerebro" cognitivo reciba una señal de audio limpia y auditada, minimizando los fallos causados por el entorno hostil de una motocicleta.

## 1. Independencia de la Percepción
THAMIS no solo procesa texto; analiza cómo fue capturado ese texto. Una señal de baja calidad (ruido de viento alto) obliga a THAMIS a ser más desconfiado y a solicitar más confirmaciones del usuario.

## 2. Componentes de la Capa
- **AudioDiagnostic:** Captura la metadata técnica (Sample rate, Bluetooth SCO, Clipping).
- **AudioQualityAnalyzer:** Calcula la relación señal-ruido (SNR) para categorizar la calidad como GOOD, MEDIUM o BAD.
- **VoicePreprocessor:** Activa supresores de ruido y controles de ganancia nativos de Android.
- **VoiceActivityDetector (VAD):** Diferencia entre silencio, voz real y ruido puro de motor/viento.

## 3. Métricas de Calidad
- **Noise Level (0.0 - 1.0):** Nivel de energía del ruido de fondo.
- **Voice Energy (0.0 - 1.0):** Potencia de la voz detectada sobre el ruido.
- **Clipping Detection:** Indica si la ganancia es demasiado alta y está distorsionando la señal.

## 4. Configuración Recomendada
Para un casco con intercomunicador, se recomienda:
- **Audio Source:** `VOICE_RECOGNITION` o `VOICE_COMMUNICATION`.
- **Sample Rate:** 16000Hz (Optimizado para Vosk).
- **Gain:** AGC activo para compensar la posición variable del micrófono.
