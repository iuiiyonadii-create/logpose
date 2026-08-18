# THAMIS Self Monitoring Engine v1.0

## 1. Propósito
El Self Monitoring Engine es el sistema de autoconciencia técnica de THAMIS. Su objetivo es evaluar continuamente la salud del cerebro cognitivo, detectando degradaciones de rendimiento, errores recurrentes o anomalías de contexto antes de que afecten la seguridad del conductor.

## 2. Componentes Principales
- **HealthMonitor**: Calcula puntajes de salud (0-100) para cada dominio.
- **PerformanceMonitor**: Rastrea latencias de ejecución y uso de recursos.
- **AnomalyDetector**: Identifica patrones fuera de los parámetros normales (ej. latencia > 1s).
- **RecoveryAdvisor**: Sugiere acciones correctivas (limpieza de caché, reinicio de módulos).
- **TelemetryCollector**: Repositorio central de datos brutos de ejecución.

## 3. Niveles de Salud Global
- **EXCELLENT**: Todo nominal.
- **GOOD**: Latencias leves o errores aislados recuperados.
- **WARNING**: Degradación visible, requiere atención del sistema.
- **CRITICAL**: Fallos en dominios clave (Navegación/Comunicación).
- **OFFLINE**: Cerebro inoperativo.

## 4. Garantía de Independencia
El motor es 100% Kotlin puro. No utiliza APIs de Android para sus mediciones, garantizando que el monitoreo sea tan portátil y determinístico como el resto del cerebro.
