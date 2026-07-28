# THAMIS Navigation Provider Architecture v1.0

## 1. Arquitectura de Abstracción
La navegación en THAMIS v3.1 ha sido completamente desacoplada mediante una capa de proveedores. El cerebro cognitivo ya no conoce aplicaciones específicas como Google Maps o Waze.

## 2. Flujo de Ejecución
1. **DecisionEngine:** Resuelve la intención de navegación (ej: `GO_HOME`).
2. **AuthorityValidator:** Verifica la seguridad, velocidad y confianza.
3. **NavigationActuator:** Recibe la orden y solicita un proveedor al Factory.
4. **ProviderFactory:** Selecciona el mejor proveedor disponible según el estado del sistema.
5. **NavigationProvider:** Traduce la orden cognitiva en un Intent de Android o acción específica.

## 3. Beneficios
- **Intercambiabilidad:** Se pueden añadir nuevos proveedores (HERE, TomTom) sin tocar el núcleo cognitivo.
- **Robustez:** Si un proveedor falla, el sistema cae automáticamente al siguiente en la lista de prioridades (Fallback).
- **Pureza:** Toda la complejidad de las APIs de Android queda confinada en los archivos de `provider` y `actuator`.

## 4. Próximos Pasos
- Implementar ruteo real mediante Deep Links para Google Maps y Waze.
- Añadir monitorización de salud (Health Score) para cada proveedor.
