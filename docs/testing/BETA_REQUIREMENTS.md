# MRTA: Requisitos para la Beta LogPose (Beta Readiness)

Este documento define qué debe estar funcionando, qué está fallando y qué es necesario para el lanzamiento de la primera beta con repartidores.

## 1. Módulos Críticos (Must-Have)

### ✅ VOZ (THAMIS 1.0) - [SOLUCIONADO/ROBUSTO]
- [x] Motor en cascada (Nivel 0, 1, 2) para evitar Error 12/10.
- [x] Inteligencia de similitud (Jaccard) para modismos argentinos.
- [x] Extracción de parámetros mediante Regex dinámicos.
- [x] Memoria de corto plazo (Contexto Sí/No).

### 🛠️ BLUETOOTH - [FALLANDO/PENDIENTE]
- [ ] Conexión automática estable con V6 Pro+.
- [ ] Reconocimiento de botones del intercomunicador como disparadores de THAMIS (en lugar de botón en pantalla).
- [ ] Recuperación automática de enlace perdido.

### ✅ APPS EXTERNAS - [SOLUCIONADO]
- [x] Apertura de WhatsApp, Instagram, YouTube, TikTok.
- [x] Apertura de Google Maps.

## 2. Lo que está Fallando (Blockers)

1.  **Muerte en segundo plano:** En algunos dispositivos (Samsung/Motorola), el micrófono se apaga a los 10 minutos aunque la notificación esté visible.
    - *Acción:* Migrar a un `Service` con `BIND_ALLOW_WHITELIST_MANAGEMENT`.
2.  **Conflicto de Audio:** Si el usuario está escuchando Spotify, LogPose a veces no "baja el volumen" para escuchar al usuario (Audio Focus).
    - *Acción:* Implementar `AudioManager.OnAudioFocusChangeListener`.

## 3. Requisitos de Arquitectura para el Crecimiento

Para que LogPose pueda crecer sin volverse un desastre, la arquitectura debe ser:
1.  **Plugin-Based:** Agregar una nueva app soportada debe ser solo añadir una línea en `AppManager`, nunca tocar el núcleo.
2.  **Brand-Agnostic:** Las clases de "Marca" (XiaomiFixer, SamsungFixer) deben estar en un módulo de compatibilidad separado.
3.  **Entity-First:** THAMIS debe pasar objetos `Contact`, `Location`, `Song` en lugar de simples textos.

## 4. Checklist de Lanzamiento Beta
- [ ] Implementar Vosk como motor Offline real para Huawei/Zonas sin señal.
- [ ] Pantalla de Configuración para descargar modelos de voz.
- [ ] Tutorial rápido para el repartidor ("Cómo hablarle a LogPose").
