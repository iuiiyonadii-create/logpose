# THAMIS Latency Model v1.0

## 1. Definiciones
- **Processing Latency**: Tiempo que tarda un motor en tomar una decisión.
- **Dispatch Latency**: Tiempo que tarda el mensaje en viajar por el EventBus.
- **Actuator Latency**: Tiempo que tarda el hardware (Spotify/Maps) en reaccionar.

## 2. Jerarquía de Tiempos Objetivos
| Dominio | Latencia Objetivo | Límite Crítico |
| :--- | :---: | :---: |
| WorldModel | 5ms | 20ms |
| Planning | 10ms | 50ms |
| Dialog | 100ms | 500ms |
| Navigation | 200ms | 1000ms |

## 3. Acumulación
El `LatencyAnalyzer` suma las latencias de cada paso del pipeline de integración para entregar el "Time to Voice" (TTV) real percibido por el conductor.
