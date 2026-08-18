# THAMIS Automated Mass Testing Engine v1.0

## 1. Propósito
El motor de pruebas masivas permite a THAMIS autoevaluarse mediante la ejecución de miles de escenarios virtuales sin intervención humana. Su objetivo principal es garantizar que cada cambio en el código no introduzca errores inesperados o degradaciones de rendimiento (regresiones).

## 2. Componentes Clave
- **AutomatedTestEngine**: Orquestador que ejecuta lotes (batches) de escenarios y genera reportes consolidados.
- **ScenarioGenerator**: Generador combinatorio de situaciones (ej. 10 variaciones de BT + 5 niveles de ruido de viento).
- **RegressionAnalyzer**: Motor comparativo que detecta si la versión actual es más lenta o consume más memoria que la anterior.
- **TestScheduler**: Programador de ejecuciones periódicas (ej. pruebas nocturnas masivas).

## 3. Filosofía de "Zero Regression"
LogPose mantiene una política estricta: un cambio no se considera aprobado si el `RegressionAnalyzer` detecta una desviación negativa superior al 15% en las latencias core del sistema.

## 4. Integración CI/CD
Los reportes generados (`DailyTestReport`) están diseñados para ser consumidos por el Desktop Lab, permitiendo al equipo de ingeniería visualizar tendencias de calidad a lo largo del tiempo.
