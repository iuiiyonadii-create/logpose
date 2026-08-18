# THAMIS Event Bus v1.0

## 1. El Latido del Sistema
El `EventBus` permite una arquitectura reactiva. Los motores pueden suscribirse a eventos de interés (ej. `NavigationStarted`) para ajustar su comportamiento automáticamente.

## 2. Eventos Críticos
- `IntentDetected`: El motor de lenguaje identificó una orden.
- `PlanCreated`: El planificador generó una ruta de ejecución.
- `JourneyStarted`: El motor de viaje detectó movimiento continuo.
- `CallStarted`: Se inició una comunicación por voz.

## 3. Desacoplamiento de UI
El `EventBus` permite que la interfaz de Android (Compose) se actualice sola al escuchar eventos cognitivos, sin que el cerebro sepa que existe una pantalla.
