# THAMIS ORCHESTRATION ENGINE v1.0 - ARQUITECTURA

## 1. Visión General
El motor de orquestación global es el director de orquesta de THAMIS. Su responsabilidad es coordinar la ejecución de acciones entre todos los dominios cognitivos (Navegación, Música, Comunicación) basándose en prioridades dinámicas y el estado del mundo.

## 2. Componentes Clave
- **GlobalOrchestrationEngine**: Orquestador central.
- **ActionScheduler**: Toma la decisión de ejecución inmediata, espera o cancelación.
- **PriorityQueueManager**: Mantiene la cola de acciones pendientes ordenada por relevancia.
- **PriorityPolicy**: Define cómo cambia la prioridad según el contexto (ej. velocidad alta).
- **InterruptPolicy**: Define las reglas para que una acción nueva pause a una en ejecución.

## 3. Flujo de Trabajo
1. El `DecisionEngine` sugiere una acción.
2. El orquestador recibe la `PendingAction`.
3. Se consulta el `WorldState` para calcular la prioridad efectiva.
4. El `ActionScheduler` evalúa si el sistema está ocupado.
5. Se decide: `EXECUTE_NOW`, `WAIT` o `IGNORE`.
6. Al finalizar una acción, se procesa el siguiente elemento de la cola.

## 4. Estándares
- **Pure Kotlin**: Sin dependencias de plataforma.
- **Decoupled**: Los dominios no conocen la existencia de otros; solo envían peticiones.
