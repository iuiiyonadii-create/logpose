# THAMIS Real Hardware Validation Lab v1.0

## 1. Propósito
El Hardware Validation Lab es la infraestructura encargada de probar THAMIS en dispositivos físicos reales bajo condiciones controladas antes de la fase beta. Su objetivo es detectar problemas específicos de fabricantes, calidades de micrófono y comportamientos de la pila Bluetooth que los simuladores no pueden recrear.

## 2. Áreas de Evaluación
- **Voz**: Pruebas de precisión de micrófono con ruido de motor real.
- **Bluetooth**: Estabilidad de conexión con diferentes marcas de intercomunicadores (Cardo, Sena, Midland).
- **Energía**: Medición real del consumo de batería durante 8 horas de uso continuo.
- **Térmico**: Monitoreo de la temperatura del dispositivo durante la ejecución del core cognitivo y GPS.

## 3. Clasificación de Compatibilidad
- **COMPATIBLE**: Android 8.0+, RAM 4GB+, Micrófono de alta sensibilidad.
- **WARNING**: Dispositivos con poca RAM o capas de personalización (Xiaomi/Samsung) agresivas con los procesos en segundo plano.
- **INCOMPATIBLE**: Versiones de Android inferiores a la 26 o hardware con Bluetooth inestable.

## 4. Metodología de Prueba
Se utilizan dispositivos de gama baja, media y alta en un "test bench" donde se automatiza la entrada de audio y se registra la respuesta física del hardware.
