# THAMIS Cache System v1.0

## 1. Time-to-Live (TTL)
Todos los datos volátiles en el cerebro de THAMIS (contextos de diálogo, resoluciones de contacto temporales) poseen un TTL obligatorio. Por defecto, los datos expiran a los 60 segundos si no son accedidos.

## 2. Limpieza Segura
El `CacheManager` garantiza que la limpieza nunca ocurra durante un estado de `WAITING_CONFIRMATION`, asegurando que la respuesta del usuario siempre tenga el contexto necesario para ser procesada.
