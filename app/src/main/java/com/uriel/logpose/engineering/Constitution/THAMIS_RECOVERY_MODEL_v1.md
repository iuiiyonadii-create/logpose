# THAMIS Recovery Model v1.0

## 1. Definiciones de Datos
- **RecoveryPlan**: Contenedor maestro del proceso de resiliencia. Incluye la anomalía de origen, el riesgo calculado y la fecha de expiración.
- **RecoveryAction**: Paso técnico individual. Define qué se hará (ej: `REBIND_SERVICE`) y el resultado que se espera obtener.
- **RecoveryDecision**: Estado final del razonamiento. Determina si el plan es `APPROVED`, `REJECTED` (por riesgo) o `DEFERRED` (esperando mejores condiciones).

## 2. Ciclo de Vida del Plan
1. **Creation**: Nacimiento en el `AutoRecoveryEngine`.
2. **Validation**: Verificación de coherencia interna.
3. **Approval**: Paso por el `PolicyEngine`.
4. **Execution Slot**: Asignación de turno por el Orquestador Global (fase futura).
5. **Auditing**: Registro permanente en el `RecoveryHistory`.
