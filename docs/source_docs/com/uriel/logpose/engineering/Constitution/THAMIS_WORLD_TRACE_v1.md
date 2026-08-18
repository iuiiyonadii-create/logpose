# THAMIS WORLD TRACE - SISTEMA DE AUDITORÍA v1.0

## 1. Estructura de la Traza
Cada registro de cambio (`WorldTrace`) captura:
- **Timestamp**: Instante exacto del cambio.
- **Affected Domain**: Qué parte del mundo cambió.
- **Origin**: Qué componente disparó la actualización.
- **State Delta**: Diferencia entre el estado anterior y el nuevo (disponible vía snapshots).

## 2. Logs de Depuración
El sistema utiliza etiquetas estandarizadas para Logcat:
- `THAMIS_WORLD`: Ciclo de vida general.
- `THAMIS_WORLD_VALIDATION`: Alertas de inconsistencia.
- `THAMIS_WORLD_METRICS`: Rendimiento del motor de contexto.
