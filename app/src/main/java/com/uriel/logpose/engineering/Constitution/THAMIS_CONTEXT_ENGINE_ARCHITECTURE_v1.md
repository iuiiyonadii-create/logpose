# THAMIS v3.4 - Context Engine Architecture

## 1. Visión General
El Context Engine es el sistema de memoria de corto plazo de THAMIS. Permite que el asistente no sea un procesador de comandos aislados, sino un copiloto consciente de la sesión viva del conductor.

## 2. Componentes del Contexto
- **SessionContext**: Objeto central que almacena el foco actual, el último comando, y la pila de estados de conversación.
- **FocusManager**: Determina qué dominio (Navegación, Música, Llamada) tiene el control prioritario de la atención del usuario.
- **PendingAction**: Almacena intenciones que requieren confirmación (ej: "¿Querés llamar a Juan?").
- **ContextSnapshot**: Registro histórico de las últimas 50 situaciones del mundo (velocidad, GPS, batería) para análisis de tendencias.

## 3. Estados de Conversación
THAMIS evoluciona a través de los siguientes estados:
1. `IDLE`: Reposo.
2. `LISTENING`: Capturando audio.
3. `PROCESSING`: Analizando intención.
4. `WAITING_CONFIRMATION`: Esperando "Sí/No".
5. `EXECUTING`: Realizando la acción.

## 4. Gestión de Expiración
Toda la información en el motor tiene un tiempo de vida (ContextLifetime):
- Conversación activa: 20 segundos.
- Confirmación pendiente: 20 segundos.
- Última canción: 2 minutos.

## 5. Recuperación
El motor permite reconstruir el estado de la sesión si el servicio de Android es reiniciado, garantizando que no se pierdan confirmaciones críticas en medio de la ruta.
