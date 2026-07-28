# THAMIS Planning Engine v1.0

## 1. Entrada y Salida
- **Entrada**: Objetivo Cognitivo (Goal) + WorldSnapshot.
- **Salida**: ExecutionPlan estructurado con pasos atómicos.

## 2. Generación de Pasos (Steps)
Cada plan descompone el objetivo en al menos tres fases:
1. **Validación**: Comprobación de pre-condiciones.
2. **Preparación**: Ajuste de hardware (audio/BT).
3. **Ejecución**: Acción final en el dominio destino.

## 3. Lógica Inmutable
El planificador nunca lee estados vivos. Utiliza exclusivamente el Snapshot proporcionado en la entrada para asegurar que el plan sea coherente desde su creación hasta su encolado.
