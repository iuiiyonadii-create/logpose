# THAMIS Confidence System v1.0

## 1. Niveles de Confianza
- **HIGH_CONFIDENCE** (>= 0.85): Ejecución directa de la intención.
- **MEDIUM_CONFIDENCE** (0.60 - 0.84): THAMIS solicita confirmación antes de actuar.
- **LOW_CONFIDENCE** (< 0.60): THAMIS solicita al usuario repetir el comando.

## 2. Factores de Cálculo
El score de confianza se compone de:
1. Confianza del motor STT (Android/Vosk).
2. Peso fonético (Coincidencia con frases canónicas).
3. Coherencia contextual (¿Tiene sentido la orden en este momento?).
4. Nivel de ruido ambiental estimado.
