# THAMIS Phonetic Model v1.0

## 1. Soporte Rioplatense
El sistema está optimizado para el habla de Argentina y Uruguay, reconociendo el voseo y las conjugaciones locales:
- "Poné" -> "Pone" (Canonical)
- "Llamá" -> "Llamar" (Canonical)
- "Contestá" -> "Contestar" (Canonical)

## 2. Normalización de Verbos
Para reducir la complejidad del reconocimiento de intenciones, todos los verbos se llevan a una forma base (Canonical Form). Esto permite que "poneme", "poné", "pon" y "ponete" activen el mismo disparador de música.

## 3. Tolerancia a Errores
El modelo fonético ignora puntuaciones y acentos tras la normalización, centrando el peso en la estructura silábica de la orden.
