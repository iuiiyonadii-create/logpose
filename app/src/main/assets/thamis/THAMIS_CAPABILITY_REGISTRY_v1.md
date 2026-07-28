# THAMIS Capability Registry v1.0

## 1. Concepto
El Capability Registry es el inventario centralizado de las "habilidades" del cerebro THAMIS. Permite al sistema autodescribirse y gestionar la autoridad de forma granular por dominio.

## 2. Descriptor de Capacidad
Cada dominio (Música, Navegación, Comunicación) se registra con:
- **Estado:** AVAILABLE, RESTRICTED o UNAVAILABLE.
- **Autoridad:** Flag que indica si THAMIS puede ejecutar acciones físicas.
- **Provider:** El sistema o app que respalda la ejecución.
- **Métricas:** Latencia media y puntuación de salud (Health Score).

## 3. Dominios Registrados
- **MULTIMEDIA:** Control total de reproducción (Spotify).
- **NAVIGATION:** Control delegado vía abstracción de proveedores.
- **COMMUNICATION:** Bloqueado (Fase de Shadow Mode).
- **NOTIFICATION:** Bloqueado (Roadmap).

## 4. Auditoría
Cualquier fallo en un actuador reduce el Health Score del dominio, lo que permite a THAMIS desactivar proactivamente una capacidad si se vuelve inestable, protegiendo la experiencia del motociclista.
