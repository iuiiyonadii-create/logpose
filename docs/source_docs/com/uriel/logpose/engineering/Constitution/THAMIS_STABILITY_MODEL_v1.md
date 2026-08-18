# THAMIS Stability Model v1.0

## 1. Definición de Salud
Un módulo se considera saludable si:
1. No ha lanzado excepciones no controladas en el ciclo actual.
2. Su latencia de procesamiento está dentro del 110% del baseline.
3. El `AutoRecoveryEngine` ha podido sanar cualquier degradación previa.

## 2. Escalado de Fallos
- **Fallo Único**: Auto-recuperación transparente.
- **Fallo Repetitivo (3+)**: Marcado de módulo como "Degradado" y notificación al `ProductHardeningEngine`.
- **Fallo Crítico**: Suspensión de procesos secundarios para proteger el núcleo de navegación y seguridad.

## 3. Registro Forense
Cada inestabilidad se registra en el `HardeningAuditLog` con el contexto del `WorldSnapshot` para reconstruir la causa exacta (ej. pérdida de señal GPS o saturación de memoria).
