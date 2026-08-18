# Notification Core — CHANGELOG

## v1.0 — Sprint 04 (engineering/00_Project)

### Agregado
- `NotificationCoreManager`, `NotificationRepository`, `NotificationSession`,
  `NotificationReceiver`, `NotificationFilter`, `NotificationHistoryStore`,
  `NotificationChannelManager`, `NotificationEvents`, `NotificationState`,
  `NotificationItem`.
- `domain.models.NotificationContent`, `NotificationCategory`,
  `NotificationPriority`.
- `logprobe.notification.NotificationEventBus` (fan-out de eventos crudos).
- `NotificationParser.parseFull()` (nueva funcion, `parse()` sin cambios).
- Registro en `AppContainer` (`notificationCoreManager`, iniciado en
  `initialize()`).
- Suite de tests unitarios (JUnit puro) para Filter, HistoryStore, Session,
  Repository y los modelos de dominio.

### Modificado
- `NotificationProbeService`: ahora reenvia cada callback tambien a
  `NotificationEventBus`, ademas de su comportamiento original (sin
  cambios) hacia `NotificationCollector`/`ProbeGuard`.
- `NotificationParser`: se agrego `parseFull()`; `parse()` intacto.

### No modificado (a proposito)
- `ProbeGuard`, `ProbeSession`, `NotificationCollector`,
  `NotificationHistory` (logprobe), `NotificationDump`,
  `NotificationTimeline`: LogProbe sigue funcionando exactamente igual que
  antes de este Sprint.
