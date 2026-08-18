# THAMIS WORLD - INFORME DE VALIDACIÓN v1.0

## 1. Reglas de Consistencia Implementadas
1. **Regla de Velocidad**: Si `speed > 5km/h`, el `JourneyState` no puede estar en `OFF`.
2. **Regla de Audio**: Si `isScoOpen == true`, la ruta no puede ser `A2DP`.
3. **Regla de Navegación**: Si hay un `destination`, el GPS debe estar en estado `READY` o `SEARCHING`.

## 2. Detectores de Información Desactualizada
El motor invalida automáticamente cualquier snapshot cuyo `timestamp` supere los 60 segundos si no ha recibido actualizaciones del proveedor correspondiente.
