# Settings Core — INTEGRATION

## Acceso desde AppContainer

```kotlin
AppContainer.settingsManager
```

Se construye y se hidrata (`start()`) dentro de `AppContainer.initialize()`,
antes de que cualquier ViewModel lo necesite.

## Como una feature futura deberia guardar su propio ajuste

Settings Core no conoce claves de negocio. Cada feature es dueña de sus
propias claves y las declara donde le corresponda a ella (no aqui):

```kotlin
// Dentro de la propia feature, no de Settings Core
private const val KEY_DRIVING_MODE_ENABLED = "driving_mode_enabled"

fun isDrivingModeEnabled(): Boolean =
    AppContainer.settingsManager.getBoolean(KEY_DRIVING_MODE_ENABLED, default = false)

fun setDrivingModeEnabled(enabled: Boolean) {
    AppContainer.settingsManager.setBoolean(KEY_DRIVING_MODE_ENABLED, enabled)
}
```

## Observar cambios reactivamente

```kotlin
val state by AppContainer.settingsManager.state.collectAsState()
```

`SettingsState` expone `strings`, `booleans` e `ints` como mapas
inmutables; una feature que necesite un ajuste especifico deberia leerlo
mediante `getString/getBoolean/getInt` con su propia clave, no iterar el
mapa completo.

## Eventos de un solo disparo

```kotlin
AppContainer.settingsManager.events.collect { event ->
    when (event) {
        is SettingsCoreEvent.Changed -> // reaccionar a event.key
        is SettingsCoreEvent.Removed -> // reaccionar a event.key
        SettingsCoreEvent.Loaded -> // hidratacion inicial completada
        SettingsCoreEvent.Cleared -> // se vacio todo el almacen
    }
}
```

## Permisos requeridos

Ninguno. Settings Core solo usa `Context.getSharedPreferences`, ya
disponible sin permisos adicionales ni cambios en `AndroidManifest.xml`.

## Integracion futura con Voice / THAMIS / Driving Mode

Cualquier modulo futuro puede leer `AppContainer.settingsManager` para sus
propios ajustes persistentes sin que Settings Core dependa de ellos (mismo
criterio que Notification Core aplica con `state`/`events`).
