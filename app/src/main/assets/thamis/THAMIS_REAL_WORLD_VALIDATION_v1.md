# THAMIS Real World Validation v1.0

Este documento define el protocolo para validar el cerebro cognitivo THAMIS v3.0 en condiciones reales de conducción (calle, ruta, viento, ruido).

## 1. Objetivo de la Prueba
Medir la superioridad de THAMIS frente al sistema legado al interpretar lenguaje natural bajo estrés ambiental, sin otorgarle todavía autoridad de ejecución.

## 2. Metodología
- **Modo Sombra:** THAMIS escucha y decide en paralelo.
- **Sesiones de Validación:** Se agrupan los eventos por ID de sesión (ej: `RUTA_2_NOCHE`).
- **Input Natural:** Se prohíbe el uso de comandos rígidos. El conductor debe hablar como si lo hiciera con un copiloto humano.

## 3. Métricas (KPIs)
- **Accuracy:** Coincidencia total con la intención del usuario.
- **Positive Divergence:** Casos donde THAMIS entendió y el sistema legado devolvió UNKNOWN.
- **Negative Divergence:** Casos donde THAMIS se equivocó y el legado acertó.
- **Critical Errors:** Interpretaciones erróneas en acciones de alto riesgo (Llamadas).

## 4. Criterios de Éxito
Se considerará que THAMIS está listo para autoridad parcial si:
1. El Accuracy es > 95%.
2. Los Errores Críticos son 0.
3. La latencia media es < 300ms.
