# THAMIS Chaos & Field Simulation Lab Architecture v1.0

## 1. Propósito
El motor de laboratorio (Thamis Lab) es la infraestructura encargada de someter al asistente a condiciones extremas y fallos controlados antes de su despliegue en entornos reales. Su objetivo es garantizar la resiliencia del sistema mediante la simulación de caos (Chaos Engineering).

## 2. Componentes Clave
- **SimulationEngine**: Orquestador que ejecuta escenarios técnicos definidos (Red, BT, GPS).
- **ChaosController**: Inyecta retrasos (latencia) y errores lógicos en los módulos core.
- **EnvironmentSimulator**: Recrea ruidos ambientales (viento, motor) y degradación de sensores.
- **ScenarioManager**: Permite crear y repetir casos de prueba específicos para depuración forense.

## 3. Filosofía de "Fallo Primero"
No esperamos que el motociclista encuentre un error en la ruta. El laboratorio busca proactivamente colapsar el sistema en un entorno virtual para medir el tiempo de recuperación del `AutoRecoveryEngine`.

## 4. Garantía de Independencia
100% Kotlin puro. El laboratorio no requiere de una moto real ni de sensores físicos; utiliza inyección de estados en el `WorldModel` para engañar al sistema y observar su reacción.
