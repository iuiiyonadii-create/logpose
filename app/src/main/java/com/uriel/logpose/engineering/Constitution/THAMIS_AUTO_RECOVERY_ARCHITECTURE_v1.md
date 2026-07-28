# THAMIS Auto Recovery Architecture v1.0

## 1. Propósito
El motor de recuperación automática es el componente de THAMIS encargado de garantizar la resiliencia del sistema ante fallos o degradaciones. Su función es analizar anomalías detectadas por el Self Monitoring Engine y diseñar planes de contingencia seguros que no comprometan la atención del conductor.

## 2. Componentes Clave
- **AutoRecoveryEngine**: Orquestador central del ciclo de vida de la recuperación.
- **RecoveryStrategyEngine**: Selecciona el método de corrección (RETRY, RESET, RECONNECT, etc.) basado en el tipo de fallo.
- **RecoveryPolicyEngine**: El guardián de seguridad que aprueba o rechaza planes basándose en el riesgo del mundo (velocidad, entorno, prioridad).
- **RecoveryPlanner**: Genera el `RecoveryPlan` detallando las acciones atómicas necesarias.
- **RecoveryRollbackEngine**: Prepara la infraestructura para revertir cambios si la recuperación empeora el estado del sistema.

## 3. Flujo de Resiliencia
1. Recepción de un `DiagnosticReport` con anomalías activas.
2. Análisis individual de cada anomalía y selección de estrategia.
3. Evaluación de políticas de seguridad contra el `WorldSnapshot` actual.
4. Generación y registro de un `RecoveryPlan` auditable.

## 4. Estándares
- **100% Kotlin Puro**.
- **No-Intervention Policy**: El motor diseña el plan pero no lo ejecuta, permitiendo que la capa de integración gestione el momento exacto del reinicio.
- **Atomicidad**: Cada acción de recuperación es independiente y rastreable.
