# THAMIS WORLD CONTEXT ENGINE v1.0 - ARQUITECTURA

## 1. Propósito
El World Context Engine es el sistema nervioso central de THAMIS. Su función es unificar todos los estados fragmentados de los dominios (Música, Navegación, Viaje, Sensores) en una única fuente de verdad inmutable y coherente.

## 2. El Modelo de Datos (WorldState)
El `WorldState` es un objeto compuesto que representa el universo completo de LogPose en un instante dado:
- **DrivingState**: Dinámica física de la motocicleta.
- **AudioState**: Estado del hardware de sonido y reproducción.
- **NavigationState**: Guía de ruta y precisión geográfica.
- **JourneyState**: Ciclo de vida del viaje (semántica del recorrido).
- **CommunicationState**: Interacciones con terceros (llamadas/mensajes).
- **DeviceState**: Salud del hardware (batería, BT, internet).
- **EnvironmentState**: Contexto externo (clima, luz, tiempo).

## 3. Principios de Operación
- **Unicidad**: Solo existe una instancia activa del estado del mundo.
- **Inmutabilidad**: Las actualizaciones se realizan mediante funciones de reducción (reducers) que generan un nuevo estado.
- **Validación Continua**: El `WorldValidationEngine` detecta inconsistencias físicas (ej. ir a 100km/h con el viaje en OFF).
- **Cero Dependencia**: No utiliza ninguna API de Android, garantizando portabilidad absoluta del cerebro cognitivo.

## 4. Auditoría y Trazabilidad
Cada cambio en el mundo genera un `WorldTrace` que registra el origen del cambio, el dominio afectado y la descripción de la mutación. Esto permite reconstruir la línea de tiempo de cualquier incidente.
