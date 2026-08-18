# THAMIS WORLD STATE - MAPA DE DATOS v1.0

## 1. Definición de Dominios
| Dominio | Responsabilidad |
| :--- | :--- |
| **Driving** | Velocidad, aceleración, riesgo y nivel de atención. |
| **Audio** | Ruta de audio (SCO/A2DP), volumen, uso de micrófono. |
| **Navigation** | Próximo giro, ETA, precisión GPS. |
| **Communication** | Llamadas activas, notificaciones críticas pendientes. |
| **Device** | Estado de batería, carga, conectividad Bluetooth. |
| **Environment** | Clima, luminosidad, hora del día. |

## 2. Niveles de Riesgo Dinámicos
El `WorldState` calcula el nivel de riesgo combinando velocidad, lluvia, batería baja y distracciones (notificaciones).
- **LOW**: Operación normal.
- **MEDIUM**: Precaución requerida (lluvia o velocidad moderada).
- **HIGH**: Atención prioritaria (velocidad alta o fallos en sensores).
- **CRITICAL**: Bloqueo de interrupciones no esenciales.
