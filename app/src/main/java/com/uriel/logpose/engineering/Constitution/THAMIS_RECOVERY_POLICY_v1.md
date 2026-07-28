# THAMIS Recovery Policy v1.0

## 1. Reglas de Aprobación
THAMIS prioriza la estabilidad de la conducción sobre la recuperación inmediata.

### Filtro de Alta Velocidad
Si la velocidad es superior a **100 km/h**, se prohíbe cualquier acción de tipo `RESET` o `REBUILD` que consuma CPU excesiva o reinicie hardware crítico (Bluetooth/GPS). Solo se permiten estrategias de tipo `WAIT` o `RETRY` liviano.

### Filtro de Riesgo Crítico
Si el `WorldSnapshot` reporta un nivel de riesgo **CRITICAL**, se bloquean todos los planes de recuperación excepto aquellos que afecten directamente a la Seguridad del Conductor.

## 2. Jerarquía de Estrategias
1. **WAIT**: Pausa la ejecución esperando estabilización natural de sensores.
2. **RETRY**: Reintento simple de la última intención fallida.
3. **RECONNECT**: Reinicio del canal de comunicación con proveedores externos (Spotify/Maps).
4. **REBUILD**: Regeneración del contexto cognitivo desde el último snapshot válido.
5. **RESET**: Reinicio total de la instancia lógica del módulo afectado.
6. **ESCALATE**: Notificación al usuario y/o sistema central por fallo irrecuperable.
