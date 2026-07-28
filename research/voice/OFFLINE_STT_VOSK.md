# Investigación: Independencia del Oído (Vosk STT)

## Objetivo
Eliminar la dependencia de los servicios de voz de Google para cumplir con el principio de **Soberanía Tecnológica** y **Offline Total**.

## Herramienta Seleccionada: Vosk
Vosk es un motor de reconocimiento de voz de código abierto que soporta más de 20 idiomas y funciona totalmente offline.

### Ventajas para LogPose
1. **Offline Real:** No requiere paquetes de idioma del sistema ni conexión a internet.
2. **Personalización:** Permite definir un vocabulario específico (gramática) para mejorar la precisión en comandos como "Log", "Pone", "Encara".
3. **Liviano:** El modelo "small" para español ocupa ~40MB.

## Plan de Implementación
1. **Arquitectura Híbrida:** Mantener el motor de Google como fallback mientras se valida la estabilidad de Vosk en el Redmi 15C.
2. **Gestión de Modelos:** Integrar el modelo dentro de la carpeta `assets` de la aplicación.
3. **VoskRecognizerManager:** Nueva clase para gestionar el ciclo de vida del AudioRecord y el decodificador de Vosk.

## Estado Actual
- Dependencias añadidas al proyecto.
- Estructura base de `VoskRecognizerManager` creada.
- Pendiente: Integración con el flujo de audio en tiempo real.
