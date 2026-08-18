# THAMIS Personalization Engine Architecture v1.0

## 1. Visión General
El motor de personalización permite a THAMIS adaptar su comportamiento (voz, mensajes, notificaciones) al estilo individual de cada conductor. El objetivo es que el asistente aprenda cómo prefiere interactuar el usuario sin comprometer la seguridad ni la privacidad.

## 2. Componentes Clave
- **PreferenceManager**: Repositorio central de las preferencias cognitivas activas.
- **PreferenceLearningEngine**: Analiza el éxito de las interacciones para ajustar niveles de confianza de forma implícita.
- **AdaptationEngine**: El "filtro" final que modifica el contenido de los mensajes según el perfil del usuario.
- **PrivacyController**: El guardián de los datos, permitiendo al usuario decidir qué información puede ser aprendida por THAMIS.

## 3. Niveles de Privacidad
- **PRIVATE**: No se guarda historial ni se aprenden patrones.
- **STANDARD**: Se guardan preferencias explícitas pero no se realiza aprendizaje implícito.
- **ADAPTIVE**: THAMIS ajusta su estilo basándose en la satisfacción detectada tras las interacciones.

## 4. Garantía de Seguridad
Las preferencias del usuario nunca pueden anular las reglas de seguridad vial (Safety Gate) ni las restricciones de autoridad (Authority Gate). Si un usuario prefiere "Mensajes Largos", pero va a 120km/h, el motor de seguridad forzará la versión "SHORT" de todos modos.
