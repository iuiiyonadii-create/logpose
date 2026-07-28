# THAMIS Desktop Lab Control Center Architecture v1.0

## 1. Propósito
El Desktop Lab Control Center es una aplicación de escritorio independiente diseñada para supervisar y orquestar las simulaciones de THAMIS. Permite a los desarrolladores ejecutar baterías de pruebas, observar métricas en tiempo real y exportar informes técnicos detallados sin depender de la interfaz móvil.

## 2. Componentes Clave
- **DesktopLabEngine**: Orquestador central que comunica la interfaz de escritorio con el simulador de dispositivo real.
- **ThamisLabDashboard**: Visualización en tiempo real de la salud del sistema (CPU, RAM, Latencia, Precisión de voz).
- **ScenarioUIController**: Interfaz para cargar, editar y disparar secuencias de eventos del mundo real.
- **ReportExporter**: Generador de archivos en formatos estándar (JSON, CSV, Markdown) para integración con sistemas de CI/CD.

## 3. Filosofía de Estación de Ingeniería
El centro de control actúa como una "estación base". Mientras THAMIS "conduce" en el simulador, el desarrollador tiene visibilidad total de los logs internos (`THAMIS_LAB`, `THAMIS_CHAOS`) y puede inyectar fallos manualmente para validar la robustez.

## 4. Portabilidad
Aunque la interfaz inicial está optimizada para Windows, la arquitectura está basada en Kotlin puro para facilitar la migración a Linux y macOS en el futuro mediante Compose Multiplatform.
