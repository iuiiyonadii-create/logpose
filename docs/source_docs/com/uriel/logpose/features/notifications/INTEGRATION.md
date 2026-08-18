# Notification Core — INTEGRATION

## Acceso desde AppContainer

```kotlin
AppContainer.notificationCoreManager
```

Se inicializa y arranca (`start()`) dentro de `AppContainer.initialize()`,
antes de que cualquier ViewModel lo necesite.

## Integracion futura con Voice

Voice deberia suscribirse a `NotificationCoreManager.events`
(`SharedFlow<NotificationCoreEvent>`) y decidir, por su cuenta, cuando
anunciar un `NotificationCoreEvent.Posted` en funcion de
`item.priority`. Notification Core nunca llama a Voice directamente
(cumple el requisito "no debe depender de Voice").

## Integracion futura con THAMIS

THAMIS puede leer `NotificationCoreManager.state`
(`StateFlow<NotificationState>`) para tener contexto de que
notificaciones recibio el usuario, sin que Notification Core conozca a
THAMIS.

## Permisos requeridos

Ninguno nuevo: `NotificationProbeService` ya declara
`BIND_NOTIFICATION_LISTENER_SERVICE` en el manifiesto. El usuario debe
habilitar el acceso a notificaciones una unica vez en Ajustes; esa
habilitacion sirve tanto para LogProbe como para Notification Core.
