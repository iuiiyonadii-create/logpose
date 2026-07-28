# THAMIS Real Device Simulation Lab Architecture v1.0

## 1. Propósito
El motor de simulación de dispositivo real (Simulation Lab) es la infraestructura encargada de recrear un entorno Android virtualizado. Permite someter a THAMIS a situaciones de la vida real (llamadas, mensajes, cambios climáticos, fallos de sensores) dentro de un entorno controlado y auditable antes de las pruebas de campo.

## 2. Componentes Clave
- **DeviceSimulationEngine**: El orquestador que crea el "teléfono virtual" y dispara los eventos secuenciales de los escenarios.
- **RealWorldEventGenerator**: Generador de estímulos (llamadas, mensajes, alertas) que imitan el comportamiento de un conductor real.
- **VoiceTestLab**: Entorno específico para la validación fonética y comparativa entre el comando esperado y el reconocido.
- **ChaosController**: Inyector de fallos y latencia para medir la resiliencia del sistema ante imprevistos técnicos.
- **ManualTestConsole**: Herramienta para que el desarrollador cree y ejecute "escenarios sobre la marcha".

## 3. Filosofía de Simulación
LogPose no prueba funciones aisladas. El laboratorio simula "viajes completos" donde el éxito se mide por la capacidad de THAMIS para priorizar correctamente la información en medio de una tormenta de eventos simultáneos.

## 4. Garantía de Seguridad
Toda la simulación se realiza en un nivel lógico superior al hardware de Android, permitiendo pruebas masivas y repetitibles sin riesgo de dañar componentes físicos o saturar redes reales.
