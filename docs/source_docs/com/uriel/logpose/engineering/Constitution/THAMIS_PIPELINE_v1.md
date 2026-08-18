# THAMIS Cognitive Pipeline v1.0

## 1. Etapas del Pipeline
1. **WorldSnapshot**: Captura el estado inmutable del mundo.
2. **Dialog**: Verifica si hay una conversación activa que deba priorizarse.
3. **Intent**: Resuelve la intención del comando recibido.
4. **Planning**: Descompone el objetivo en pasos técnicos.
5. **Safety**: Valida el plan contra las leyes de seguridad física.
6. **Authority**: Verifica si THAMIS tiene permiso para ejecutar la acción.
7. **Actuator**: Dispara la ejecución en el dominio correspondiente.

## 2. Manejo de Resultados
Cada etapa puede interrumpir el pipeline devolviendo:
- `REJECT`: La acción no es segura o permitida.
- `WAIT`: El canal está ocupado, reintentar más tarde.
- `FAILURE`: Error técnico irrecuperable.
