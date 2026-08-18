# THAMIS ORCHESTRATION - POLÍTICA DE INTERRUPCIÓN v1.0

## 1. Reglas de Corte
Para que una acción nueva interrumpa a una actual, debe cumplirse al menos una condición:
1. El dominio de la nueva acción es **EMERGENCY**.
2. La prioridad de la nueva acción es al menos **100 puntos superior** a la actual.

## 2. Acciones de Bajo Impacto
Si la prioridad es similar o inferior, la acción se encola en el `PriorityQueueManager` para ser ejecutada inmediatamente después de que el slot quede libre.
