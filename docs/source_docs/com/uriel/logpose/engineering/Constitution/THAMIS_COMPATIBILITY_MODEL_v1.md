# THAMIS Compatibility Model v1.0

## 1. Alcance de Hardware
THAMIS está diseñado para ser agnóstico del dispositivo, pero el motor de compatibilidad valida:
- **Android API**: 26 (8.0) como mínimo recomendado.
- **BT Stack**: Soporte SCO (Synchronous Connection Oriented) para intercomunicadores.
- **CPU**: Capacidad de mantener un ciclo de actualización de 10Hz.

## 2. Validación de Intercomunicadores
El sistema clasifica los perfiles de hardware en:
- **GOLD**: Latencia < 200ms (Cardo/Sena modernos).
- **SILVER**: Latencia 200-500ms (Generic Bluetooth).
- **LEGACY**: Requiere buffers adicionales de silencio.
