# THAMIS CONTACT RESOLUTION v1.0

## 1. Lógica de Mapeo
El sistema utiliza una búsqueda difusa (fuzzy search) sobre el nombre y los alias de los contactos:
- "Juan" -> Puede ser "Juan Pérez" o "Juan trabajo".
- "Mamá" -> Alias común mapeado a un número específico.

## 2. Gestión de Desambiguación
Si una búsqueda devuelve múltiples resultados:
1. Se verifica si hay un contacto marcado como **Favorito**.
2. Si no hay favorito claro, el estado pasa a `WAITING_CONFIRMATION`.
3. THAMIS preguntará: "¿Querés llamar a Juan Pérez o Juan trabajo?".

## 3. Prioridad de Contactos
Cada contacto tiene un nivel de prioridad asignado por el `CommunicationMemory` basado en la frecuencia de uso, permitiendo a THAMIS "aprender" a quién se refiere el usuario habitualmente.
