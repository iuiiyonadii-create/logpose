# THAMIS LAB - DEVELOPMENT ENVIRONMENT (TLDE)

## 1. ESPECIFICACIÓN DE HARDWARE
- **CPU**: Ryzen 5 4500 (Mínimo recomendado para emulación).
- **GPU**: GTX 1660 Super (Aceleración de gráficos de emulador).
- **RAM**: 16 GB (8GB dedicados al sistema, 8GB a emuladores).
- **Optimización**: Límite de carga del 80% gestionado por el Orchestrator.

## 2. STACK TECNOLÓGICO
- **Lenguaje**: Python 3.10+ (Automatización y Orquestación).
- **Android Stack**: 
  - Android Studio Dolphin+
  - SDK Platforms: API 26 (Android 8.0) hasta API 35 (Android 15).
  - ADB (Android Debug Bridge) v1.0.41+
- **Base de Datos**: SQLite (Local) -> Preparado para PostgreSQL (Cloud).
- **Virtualización**: WSL2 (Opcional) / Windows Nativo con HAXM/AEHD.

## 3. HERRAMIENTAS DE CALIDAD DE CÓDIGO
- **Linter**: Ruff (Alta velocidad).
- **Formatter**: Black.
- **Type Checker**: MyPy.
- **Testing**: PyTest para backend, JUnit/Espresso para Android.

## 4. GUÍA DE INSTALACIÓN RÁPIDA
1. Instalar Python y Android SDK.
2. Ejecutar `setup_thamis.py` (Módulo Core).
3. Configurar `.env` con las rutas de ADB y AVD.
4. Validar con `python scripts/check_env.py`.
