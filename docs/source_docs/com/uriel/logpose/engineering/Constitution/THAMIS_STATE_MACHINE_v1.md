# THAMIS JOURNEY - MÁQUINA DE ESTADOS v1.0

## 1. Mapa de Estados
- **OFF**: Estado inicial, sin casco ni Bluetooth.
- **PREPARING**: Casco detectado, preparando GPS y sensores.
- **READY**: Sensores estables, esperando umbral de velocidad (>5km/h).
- **MOVING**: Viaje activo en curso.
- **STOPPED**: Detención temporal (semáforo, cruce).
- **PAUSED**: Pausa prolongada (>5min) o manual.
- **PARKED**: Evidencias de fin de viaje (Motor apagado, casco desconectado).
- **FINISHED**: Sesión cerrada y guardada en memoria.

## 2. Transiciones Críticas
- **READY -> MOVING**: Disparado por `SpeedChanged` o `MovementDetected`.
- **MOVING -> STOPPED**: Disparado por velocidad < 1km/h durante 5 segundos.
- **STOPPED -> PARKED**: Gatillado por `IdleTimeout` o pérdida de Bluetooth.
