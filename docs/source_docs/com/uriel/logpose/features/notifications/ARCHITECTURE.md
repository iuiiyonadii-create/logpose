# Notification Core — ARCHITECTURE

Status: OFFICIAL
Version: 1.0 (Sprint 04, ver engineering/00_Project)

## Flujo oficial

```
Compose
  |
  v
ViewModel
  |
  v
NotificationCoreManager   <- fachada, unico punto de entrada de UI
  |
  v
NotificationRepository    <- estado + historial + eventos
  |
  v
NotificationSession       <- reglas (whitelist/blacklist/driving mode/categorias)
  |
NotificationReceiver       <- traduce StatusBarNotification -> NotificationContent
  |
  v
NotificationEventBus       <- fan-out (logprobe.notification, sin logica de negocio)
  |
  v
NotificationProbeService   <- unico NotificationListenerService de Android
```

## Por que reutiliza NotificationProbeService

El Sprint original proponia un `NotificationListenerService` propio. Se
detecto que el proyecto ya registra uno (`NotificationProbeService`, de
LogProbe) y que Android permite un unico listener practico por app sin
friccion de UX (cada listener adicional exige habilitacion manual separada
en Ajustes). La decision arquitectonica oficial (CTO, Sprint 04) fue
mantener un unico servicio y convertirlo en un proveedor de eventos sin
logica de negocio, distribuyendo a multiples consumidores internos via
`NotificationEventBus`.

## Por que NO reutiliza ProbeGuard / ProbeSession

`ProbeGuard`/`ProbeSession` estan disenados para sesiones de diagnostico
manuales, con allowlist obligatoria y sin modo "capturar todo". Notification
Core es una feature de producto que debe funcionar siempre, con
"permitir salvo lista negra" por defecto. Reutilizarlos habria acoplado
el ciclo de vida de una feature de usuario final al de una herramienta de
diagnostico para desarrolladores. Por eso `NotificationSession` es una
clase nueva e independiente (ver Decision 3/4 del Sprint).

## Por que NotificationParser se extiende y no se duplica

`NotificationParser.parse()` (metadata-only, sin texto) sigue intacto y
es lo unico que usa LogProbe. Se agrego `NotificationParser.parseFull()`
como funcion nueva en la MISMA clase, que si lee el contenido visible de
la notificacion. Las dos funciones conviven en el mismo objeto sin
duplicar la clase (Decision 5).

## Modelos de dominio (`domain.models`)

- `NotificationContent`: contenido completo (title/text/subText/bigText +
  metadata). Vive en `domain.models`, no en `features.notifications`, para
  que tanto `logprobe.notification.NotificationParser` (infraestructura)
  como este modulo puedan depender de el sin invertir la direccion de
  dependencias del proyecto.
- `NotificationCategory`: categoria de producto (MESSAGE, CALL, EMAIL,
  SOCIAL, NAVIGATION, MEDIA, ALARM, SYSTEM, OTHER). No confundir con
  `android.app.Notification.CATEGORY_*` (una de sus señales de entrada) ni
  con `android.app.NotificationChannel`.
- `NotificationPriority`: CRITICAL, HIGH, NORMAL, LOW, SILENT.

## Naming

La clase raiz se llama `NotificationCoreManager`, no `NotificationManager`,
para evitar colision con `android.app.NotificationManager` (ya en uso en
`core.notifications.NotificationHelper`). Ver Decision 4 del Sprint.

`NotificationHistoryStore` (no `NotificationHistory`) para no colisionar
conceptualmente con `logprobe.notification.NotificationHistory`, que es una
vista de solo lectura sobre `ProbeTimeline` y pertenece exclusivamente a
LogProbe.

## Testing

Todo el modulo excepto `NotificationReceiver` (el unico archivo que toca
`StatusBarNotification`) trabaja solo con tipos de Kotlin puro
(`NotificationContent`, `NotificationItem`, etc.), por lo que se testea con
JUnit plano, sin Robolectric ni mocks de Android. Ver
`app/src/test/java/com/uriel/logpose/features/notifications/`.

## Riesgos conocidos / deuda registrada

- No se pudo compilar con `./gradlew build` en este entorno (sin toolchain
  de Android disponible). Revisado a mano por consistencia de sintaxis,
  paquetes e imports — igual que se documento para LogCore Providers en
  PROJECT_STATE.md. Recomendado correr el build real como primer paso
  despues de importar el proyecto, antes de congelar el modulo.
- `NotificationChannelManager` es en memoria (no persiste entre reinicios
  de proceso). Persistencia queda fuera de alcance de este Sprint.
- Integracion real con Voice/THAMIS no implementada: el modulo expone
  `state`/`events` listos para consumirse, pero ningun consumidor existe
  todavia (ver INTEGRATION.md).
