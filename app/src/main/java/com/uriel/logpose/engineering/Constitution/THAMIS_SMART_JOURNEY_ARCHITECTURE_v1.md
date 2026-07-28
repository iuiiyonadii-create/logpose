# THAMIS SMART JOURNEY ENGINE v1.0 - ARQUITECTURA

## 1. Visión General
El motor de viaje inteligente (Journey Engine) es el componente de THAMIS encargado de orquestar el ciclo de vida de un viaje en motocicleta. A diferencia de un simple velocímetro, el Journey Engine utiliza un enfoque probabilístico basado en evidencias para determinar el estado real del usuario.

## 2. Capas de la Arquitectura
- **Model**: Definición de estados (`JourneyState`), eventos (`JourneyEvent`) y transiciones.
- **State**: Máquina de estados pura Kotlin que gobierna la lógica de transiciones.
- **Engine**: Orquestador central que integra evidencias, seguridad y auditoría.
- **Security**: `JourneySafetyGate` impide estados físicamente imposibles o peligrosos.
- **Validation**: `JourneyValidationEngine` audita la calidad y consistencia de los datos recolectados.
- **Audit**: `JourneyTrace` registra cada milisegundo de cambio para reconstrucción forense.

## 3. Lógica de Evidencias
THAMIS no confía en un solo sensor. Evalúa:
- Conexión Bluetooth del Intercomunicador.
- Disponibilidad y precisión del GPS.
- Velocidad actual vs Umbral de movimiento.
- Estado de carga y batería (indica si el teléfono está en un soporte).

## 4. Estándares Cumplidos
- **Zero Android Dependency**: El núcleo puede testearse en cualquier JVM.
- **Provider Pattern**: El hardware se abstrae mediante `JourneyDataProvider`.
- **Shadow Mode Ready**: El motor puede evaluar sin disparar acciones si la confianza es baja.
