# THAMIS JOURNEY - TRAZA DE AUDITORÍA v1.0

## 1. Estructura de la Traza
Cada registro de auditoría (`JourneyTrace`) contiene:
- `Transition`: De qué estado a qué estado.
- `Trigger`: El evento exacto que lo causó.
- `Confidence Score`: Qué tan segura estaba THAMIS del cambio.
- `Sensor Snapshots`: Velocidad, GPS y BT en ese instante.

## 2. Propósito
Garantizar la transparencia total en la toma de decisiones. Si THAMIS detecta el fin de un viaje por error, la traza permitirá identificar qué sensor o evidencia falló.
