# Settings Core — ARCHITECTURE

Status: OFFICIAL
Version: 1.0 (Sprint 06, ver engineering/00_Project)

## Flujo oficial

```
Compose
  |
  v
ViewModel (futuro, no incluido en este Sprint)
  |
  v
SettingsManager      <- fachada, unico punto de entrada de UI
  |
  v
SettingsRepository   <- estado + eventos, orquesta Session y Store
  |
  +--> SettingsSession   <- config en memoria, reactiva (StateFlow)
  |
  +--> SettingsStore     <- contrato de persistencia (domain.settings)
        |
        v
       SettingsPreferences <- implementacion sobre SharedPreferences
```

Este flujo respeta exactamente PROJECT_CONTEXT.md: UI -> ViewModel ->
Manager -> Repository -> Data Source -> Android APIs.

## Por que `SettingsStore` vive en `domain.settings`

`data.preferences.SettingsPreferences` (infraestructura) y
`features.settings.*` (feature) necesitan compartir el mismo contrato de
persistencia sin invertir la direccion de dependencias del proyecto. La
misma decision ya existe en el proyecto para `domain.models.NotificationContent`
(ver `features/notifications/ARCHITECTURE.md`): el modelo/contrato
compartido vive en `domain`, y tanto la capa de infraestructura como la de
feature dependen de `domain` hacia adentro, nunca al reves.

Esto tambien hace que `SettingsRepository` sea testeable con JUnit plano:
depende de la interfaz `SettingsStore`, no de `SettingsPreferences`
(Android) directamente. Los tests usan `FakeSettingsStore`, un fake en
memoria (`app/src/test/java/com/uriel/logpose/features/settings/`).

## Por que es generico (String/Boolean/Int) y no tiene claves de negocio

El Sprint 06 pide explicitamente "Solo crear la arquitectura. No
implementar funcionalidades finales. No implementar logica de negocio."
Definir claves concretas (p. ej. "modo conduccion", "idioma preferido",
"unidades") seria logica de negocio de features especificas, que deberian
ser las dueñas de sus propias claves y llamar a
`AppContainer.settingsManager.getString/setBoolean/...` con el nombre de
clave que les corresponda. Settings Core no conoce ni valida esas claves;
solo garantiza que persistan y sean reactivas.

## Por que un archivo de SharedPreferences propio

`data.preferences.DevicePreferences` (Bluetooth) usa el archivo
`"logpose_preferences"` para la seleccion de dispositivo. Settings Core usa
un archivo separado, `"logpose_settings"`, para no mezclar una decision de
Bluetooth Core con ajustes generales de la app — mismo criterio de
aislamiento que ya aplica el proyecto entre modulos.

## Por que no se agrego una libreria de persistencia (DataStore, etc.)

`app/build.gradle.kts` no declara Jetpack DataStore ni ninguna libreria de
serializacion. Introducir una dependencia nueva solo para esta
infraestructura violaria "Prefer compatibility over refactoring" /
"Never over-engineer" (PROJECT_CONTEXT.md, PROJECT_BOOTSTRAP.md) para un
Sprint que pide explicitamente solo la infraestructura base. `SettingsPreferences`
sigue el mismo patron ya validado en el proyecto (`DevicePreferences`):
`Context.getSharedPreferences` simple.

## Modelos

- `SettingsState` (`features.settings`): snapshot inmutable observable por
  UI, separado por tipo (`strings`, `booleans`, `ints`) para evitar
  castings en la capa de presentacion.
- `SettingsSessionConfig` (`features.settings`): configuracion interna de
  `SettingsSession`, mismo shape que `SettingsState` menos `isLoaded`.
- `SettingsCoreEvent` (`features.settings`): `Changed`, `Removed`,
  `Loaded`, `Cleared`.

## Naming

`SettingsManager` no colisiona con ninguna clase existente del proyecto
(a diferencia de `NotificationCoreManager`, que evito colisionar con
`android.app.NotificationManager`), por lo que no fue necesario un nombre
alternativo.

## Testing

Todo el modulo excepto `SettingsPreferences` (el unico archivo que toca
`android.content.SharedPreferences`) trabaja solo con tipos de Kotlin puro,
por lo que se testea con JUnit plano, sin Robolectric ni mocks de Android.
Ver `app/src/test/java/com/uriel/logpose/features/settings/`:
`SettingsSessionTest`, `SettingsRepositoryTest`, `SettingsManagerTest`,
`FakeSettingsStore`.

## Riesgos conocidos / deuda registrada

- No se pudo compilar con `./gradlew build` en este entorno (sin toolchain
  de Android disponible). Revisado a mano por consistencia de sintaxis,
  paquetes e imports — mismo criterio documentado para LogCore Providers y
  Notification Core en PROJECT_STATE.md / features/notifications/ARCHITECTURE.md.
  Recomendado correr el build real como primer paso despues de importar
  este Sprint, antes de congelar el modulo.
- Solo soporta String/Boolean/Int. Float, Long y Set<String> quedan fuera
  de alcance de esta version (`SettingsStore.snapshot()` los ignora
  silenciosamente durante `hydrate()`).
- `SettingsScreen` es un skeleton visual (titulo + estado de carga + conteo
  de claves), sin ninguna fila de ajuste ni logica de negocio, tal como
  pide el Sprint. No esta registrada en ninguna navegacion/Activity: la
  integra un Sprint futuro cuando exista UI final que la necesite.
- Ver informe de hallazgos de documentacion en la entrega de este Sprint:
  `engineering/00_Project/PROJECT_STATE.md` contiene marcadores de merge de
  Git sin resolver (`<<<<<<< HEAD` / `=======` / `>>>>>>>`), y ni
  `features.notifications` (ya implementado) ni `features.settings` (este
  Sprint) figuran en la lista de "Pending Modules" / "MODULES" de
  `PROJECT_STATE.md` / `PROJECT_CONTEXT.md`. No bloquea este Sprint (ver
  informe adjunto), pero se deja registrado para que el CTO lo resuelva.
