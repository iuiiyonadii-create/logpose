# THAMIS CAPABILITY REGISTRY v1.0

## Propósito
El `CapabilityRegistry` actúa como el inventario central de las habilidades del cerebro cognitivo. Permite a THAMIS conocer su propio estado y las herramientas que tiene disponibles para interactuar con el mundo real.

## Capacidades Registradas
- **MULTIMEDIA**: Control de música y audio (Spotify).
- **NAVIGATION**: Rutas y mapas (Google Maps, Waze).
- **COMMUNICATION**: Llamadas y mensajes (Phone, WhatsApp).

## Atributos de Descriptor
Cada capacidad rastrea:
- **Provider**: El motor de ejecución actual.
- **Health Score**: Salud del dominio (0.0 - 1.0).
- **Average Latency**: Tiempo promedio de respuesta.
- **Availability**: Si la capacidad está lista para ser usada.

## Gestión de Salud
El `CapabilityManager` supervisa el `errorCount` de cada dominio. Si una capacidad presenta fallos recurrentes, su `Health Score` disminuye y puede disparar alertas de calibración.

## Extensibilidad
Nuevos dominios como `VEHICLE` o `IOT_CONTROL` pueden registrarse dinámicamente cumpliendo con el patrón Descriptor/Provider/Actuator.
