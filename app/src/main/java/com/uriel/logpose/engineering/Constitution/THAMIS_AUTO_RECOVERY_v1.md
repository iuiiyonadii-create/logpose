# THAMIS Auto Recovery Engine v1.0

## 1. Propósito
El motor de recuperación automática es el sistema encargado de diseñar planes de contingencia cuando se detectan anomalías en el cerebro de THAMIS. Su objetivo es garantizar la resiliencia del asistente sin comprometer la seguridad del motociclista.

## 2. Componentes Principales
- **AutoRecoveryEngine**: Orquestador central que analiza anomalías y contextos.
- **RecoveryStrategyEngine**: Selecciona el enfoque de recuperación (Soft Reset, Hard Reset, etc.).
- **RecoveryPolicyEngine**: El guardián de seguridad que aprueba o rechaza planes basándose en el riesgo real.
- **RecoveryActionPlanner**: Descompone estrategias en pasos atómicos de ejecución técnica.

## 3. Filosofía de Resiliencia
THAMIS no reacciona ciegamente ante un error. Cada plan de recuperación es evaluado contra el `WorldSnapshot` actual. Si el conductor va a alta velocidad o está en medio de un giro, las recuperaciones agresivas se posponen para evitar distracciones.

## 4. Garantía de Seguridad
El motor es 100% Kotlin puro y opera en modo consultivo. Genera planes estructurados pero no dispara la ejecución física de reinicio, la cual es delegada a la capa de integración.
