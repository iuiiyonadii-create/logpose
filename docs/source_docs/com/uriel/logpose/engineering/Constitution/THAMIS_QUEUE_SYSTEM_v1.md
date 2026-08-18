# THAMIS ORCHESTRATION - SISTEMA DE COLA v1.0

## 1. Características
- **Priority-Sorted**: Las acciones con mayor prioridad efectiva se mueven al inicio.
- **De-duplication**: Evita que se encolen el mismo comando o intención varias veces.
- **Auto-Expiration**: Las acciones en cola que superan los 10 segundos son eliminadas automáticamente.

## 2. Gestión de Slot
El sistema permite un único slot de ejecución cognitiva principal, garantizando que el usuario nunca reciba dos órdenes auditivas simultáneas.
