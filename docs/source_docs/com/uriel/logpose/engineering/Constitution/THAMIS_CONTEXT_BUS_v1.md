# THAMIS Context Bus v1.0

## 1. Propósito
El `ContextBus` es la memoria compartida de THAMIS. Permite que cualquier motor publique datos de estado que otros motores puedan necesitar, sin crear acoplamientos directos.

## 2. Claves Estándar
- `CURRENT_SPEED`: Velocidad actual del vehículo.
- `IS_CALL_ACTIVE`: Estado de la telefonía.
- `ACTIVE_NAV_DESTINATION`: Destino actual de Maps/Waze.
- `CURRENT_TRACK`: Información de la música en reproducción.

## 3. Seguridad
El Bus es de acceso concurrente seguro (ConcurrentHashMap), garantizando que las lecturas y escrituras no bloqueen el hilo principal de procesamiento cognitivo.
