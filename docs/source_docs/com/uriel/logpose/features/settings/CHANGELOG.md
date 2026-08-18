# Settings Core — CHANGELOG

## v1.0 — Sprint 06 (engineering/00_Project)

### Agregado

- `SettingsManager`, `SettingsRepository`, `SettingsSession`,
  `SettingsState`, `SettingsCoreEvent` (`features.settings`).
- `SettingsStore` (`domain.settings`): contrato de persistencia generico
  (String/Boolean/Int).
- `SettingsPreferences` (`data.preferences`): implementacion de
  `SettingsStore` sobre `Context.getSharedPreferences`
  (`"logpose_settings"`).
- `SettingsScreen` (`features.settings`): skeleton Compose, solo estado de
  infraestructura, sin filas de ajuste ni logica de negocio.
- Registro en `AppContainer` (`settingsManager`, construido e hidratado —
  `start()` — dentro de `initialize()`).
- Suite de tests unitarios (JUnit puro): `SettingsSessionTest`,
  `SettingsRepositoryTest`, `SettingsManagerTest`, `FakeSettingsStore`.
- Documentacion: `README.md`, `ARCHITECTURE.md`, `CHANGELOG.md`,
  `INTEGRATION.md`.

### Modificado

- `AppContainer.kt`: se agregaron los imports de `SettingsPreferences` y
  `SettingsManager`, la propiedad `settingsManager` y su construccion +
  `start()` dentro de `initialize()`. Ningun miembro existente
  (`bluetoothRepository`, `bluetoothSessionManager`, `providerRegistry`,
  `providerModules`) fue modificado ni reordenado.

### No modificado (a proposito)

Bluetooth, Notification, Voice, Music, Calls, THAMIS, LogPoseEngine:
ninguno de estos modulos fue tocado por este Sprint (ver Sprint 06 / NO
MODIFICAR). `AndroidManifest.xml` tampoco requirio cambios: Settings Core
no agrega permisos, services ni receivers nuevos.
