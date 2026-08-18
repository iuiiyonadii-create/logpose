# THAMIS Global Priority Engine v1.0

## 1. Jerarquía Absoluta
LogPose respeta el siguiente orden de atención para garantizar la seguridad del conductor:
1. **EMERGENCY**: Accidentes y alertas de vida.
2. **SAFETY**: Radares, velocidad excesiva, batería crítica.
3. **CALL**: Comunicaciones de voz activas.
4. **NAVIGATION**: Instrucciones de giro inmediatas.
5. **DIALOG**: Conversaciones activas con el asistente.
6. **COMMUNICATION**: Notificación de mensajes nuevos.
7. **MULTIMEDIA**: Información de Spotify / YouTube Music.

## 2. Lógica de Arbitraje
Si dos eventos ocurren simultáneamente, el `GlobalPriorityEngine` otorga el canal de audio al que tenga mayor nivel, encolando el segundo para ser leído cuando el foco sea liberado.
