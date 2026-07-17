# Notification Core

Infraestructura oficial de notificaciones de LogPose. Independiente de
Bluetooth, Voice y THAMIS (se integraran a futuro consumiendo `state`/
`events`, nunca al reves).

## Que hace

- Escucha notificaciones del sistema (via `NotificationProbeService`,
  compartido con LogProbe).
- Filtra por lista blanca / lista negra de paquetes.
- Habilita/deshabilita categorias completas (mensajes, llamadas, medios...).
- Categoriza y prioriza cada notificacion.
- Mantiene un historial acotado en memoria con estado de lectura.
- Expone modo conduccion como flag de configuracion.

## Como se usa

```kotlin
val manager = AppContainer.notificationCoreManager

// Compose / ViewModel
val state by manager.state.collectAsState()

manager.setWhitelist(setOf("com.whatsapp", "com.google.android.apps.maps"))
manager.setDrivingMode(true)
manager.markAsRead(notificationId)
```

Ver `ARCHITECTURE.md` para el diseño completo y `INTEGRATION.md` para
como conectar Voice/THAMIS a futuro.
