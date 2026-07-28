# THAMIS Safety Intelligence & Riding Context Architecture v1.0

## 1. Visión General
El motor de inteligencia de seguridad es el componente encargado de interpretar la situación física del motociclista. Su objetivo no es controlar la conducción, sino adaptar la carga de interacción (voz, notificaciones, música) para garantizar que la atención se mantenga en la ruta.

## 2. Componentes Clave
- **RiskAssessmentEngine**: Clasifica el riesgo físico (LOW a CRITICAL) basándose en la velocidad y complejidad de la ruta.
- **AttentionEstimator**: Calcula la carga cognitiva estimada del usuario según sus interacciones actuales.
- **SafetyDecisionEngine**: Determina si se debe permitir, retrasar o cancelar una interacción basada en el riesgo.
- **RidingContext**: Modelo unificado que captura la realidad de la moto en cada micro-decisión.

## 3. Filosofía de No-Distracción
LogPose opera bajo la regla del 1%. El motor de seguridad fuerza el modo `SIMPLIFY` o `CANCEL` si detecta que la carga de información está saturando al conductor o si la velocidad es crítica (>120km/h).

## 4. Garantía de Independencia
100% Kotlin puro. El motor razona sobre datos numéricos y estados semánticos, desacoplándose de los sensores específicos de Android para asegurar un comportamiento determinístico y auditable.
