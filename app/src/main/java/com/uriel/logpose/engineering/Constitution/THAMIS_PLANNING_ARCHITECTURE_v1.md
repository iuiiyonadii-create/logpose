# THAMIS Planning Architecture v1.0

## 1. Visión General
El motor de planificación transforma las decisiones directas en planes estructurados. Su objetivo es decidir no solo el "qué" sino el "cómo" y el "cuándo" óptimos, basándose en la seguridad del motociclista.

## 2. Componentes Clave
- **PlanningEngine**: Orquesta la creación de planes dividiendo objetivos en pasos.
- **RuleEngine**: Aplica restricciones físicas (ej. no leer mensajes si hay una llamada).
- **StrategyEngine**: Selecciona el enfoque de ejecución (SAFEST, FASTEST, etc.).
- **Scheduler**: Gestiona el tiempo de ejecución y procesa la cola de planes.
- **RollbackEngine**: Garantiza que el sistema pueda volver a un estado seguro si un plan falla.

## 3. Estados del Plan
1. **PENDING**: Plan creado pero en cola.
2. **RUNNING**: Paso actual en ejecución.
3. **COMPLETED**: Ejecución exitosa.
4. **FAILED**: Gatillo para el Rollback.

## 4. Garantía de Seguridad
El motor es 100% determinístico y opera sobre Snapshots inmutables del mundo, garantizando consistencia total en el razonamiento.
