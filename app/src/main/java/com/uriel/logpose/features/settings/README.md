# Settings Core

Infraestructura oficial de ajustes de LogPose (Sprint 06, Settings Core
Foundation). Independiente de Bluetooth, Notification, Voice, Music, Calls,
THAMIS y LogPoseEngine — ninguno de esos modulos fue tocado por este Sprint.

## Que hace

- Expone un almacen de ajustes generico, tipado por String/Boolean/Int.
- Persiste cada cambio inmediatamente en disco (SharedPreferences).
- Mantiene un estado en memoria reactivo (`StateFlow<SettingsState>`) para
  que la UI no dependa de I/O sincrono.
- Emite eventos puntuales (`SettingsCoreEvent`) para cambios, borrados y
  ciclo de carga.

## Que NO hace (fuera de alcance de este Sprint)

Este Sprint construye unicamente la infraestructura (Manager, Repository,
Session, State, Preferences, Events, Screen). No define ninguna clave de
ajuste de negocio (idioma, modo conduccion, unidades, etc.), ni pantallas de
UI final: eso corresponde a un Sprint futuro que consuma esta base.

## Como se usa

```kotlin
val manager = AppContainer.settingsManager

// Compose / ViewModel
val state by manager.state.collectAsState()

manager.setBoolean("driving_mode_enabled", true)
manager.getString("preferred_language", default = "es")
manager.remove("some_key")
```

Ver `ARCHITECTURE.md` para el diseño completo y `INTEGRATION.md` para como
conectar features futuras.
