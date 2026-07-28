# THAMIS NAVIGATION PROVIDER ARCHITECTURE v1.0

## Arquitectura
La navegación en THAMIS v3.0 está completamente desacoplada mediante una capa de abstracción de proveedores. El núcleo cognitivo (pure Kotlin) no conoce implementaciones específicas de Android, Google Maps o Waze.

## Flujo Completo
1. **Decision Engine**: THAMIS decide navegar.
2. **Authority Gate**: Verifica si el dominio NAVIGATION está autorizado.
3. **Domain Validator**: `NavigationAuthorityValidator` valida seguridad, confianza y contexto.
4. **Actuator**: `NavigationActuator` solicita el mejor proveedor disponible.
5. **Provider**: `NavigationProvider` (GoogleMaps, Waze o Legacy) ejecuta el Intent de Android.
6. **Android**: Abre la aplicación externa.

## Providers
- **GoogleMapsProvider**: Integración mediante Intents `google.navigation:q=`.
- **WazeProvider**: Integración mediante deep-links `waze://`.
- **LegacyNavigationProvider**: Fallback seguro para mantener compatibilidad con el sistema anterior.

## Gateways & Actuators
- **NavigationActuator**: Traduce decisiones cognitivas a llamadas de interfaz de proveedor.
- **ActuationGateway**: Puerta final de control de ejecución.

## Validators
- **NavigationAuthorityValidator**: Aplica reglas de negocio y seguridad (Velocidad, GPS, Llamadas activas).

## Seguridad
- Bloqueo duro a velocidades > 120 km/h.
- Confirmación requerida entre 100-120 km/h.
- Bloqueo si el GPS no es confiable.

## Fallback
Si el proveedor principal falla o la autoridad cognitiva es denegada, el sistema puede redirigir al flujo de navegación legado para asegurar que el usuario nunca pierda la funcionalidad básica.

## Métricas
- **Latencia**: Tiempo desde la decisión hasta la apertura de la app externa.
- **Health Score**: Basado en la tasa de éxito de los proveedores.

## Riesgos
- Cambios en las APIs de Intent de terceros (Google/Waze).
- Inestabilidad del GPS en túneles que bloqueen la autoridad.

## Próximas Fases
- Integración de direcciones desde contactos.
- Control de navegación por voz avanzado (Siguiente paso, repetir instrucción).
