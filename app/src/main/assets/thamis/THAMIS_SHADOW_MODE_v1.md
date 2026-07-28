# THAMIS Shadow Mode v1.0

El modo sombra es la fase de validación en la que el cerebro cognitivo v3.0 observa el comportamiento del sistema legado para aprender y medirse en condiciones reales de conducción.

## 1. ¿Por qué no ejecuta?
Para garantizar la seguridad del motociclista, THAMIS v3.0 no tiene permiso de ejecución física (Actuation). Su salida se desvía al `ShadowLogger`, permitiendo que el desarrollador analice las divergencias sin riesgo de comandos erróneos.

## 2. Comparación de Decisiones
Cada frase recibida dispara dos procesos:
- **Legacy:** El parser lineal original que toma la acción real.
- **Cognitivo (Shadow):** El pipeline de Hipótesis y Evidencia.

Se registra un `MATCH` si ambos coinciden y una `DIVERGENCE` si THAMIS propone algo distinto. Esto es vital para detectar si THAMIS es demasiado prudente (muchos CONFIRM) o demasiado impulsivo.

## 3. Uso de Datos Reales
A diferencia de los tests unitarios, el Shadow Mode utiliza el `WorldState` real (velocidad, música, batería), lo que nos permite refinar los pesos de los `EvidenceEvaluators` basándonos en el ruido y estrés real de la ruta.
