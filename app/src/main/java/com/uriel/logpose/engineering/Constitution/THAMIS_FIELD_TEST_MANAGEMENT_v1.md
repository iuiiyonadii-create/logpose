# THAMIS Field Test Management System Architecture v1.0

## 1. Propósito
El motor de gestión de pruebas de campo es el puente entre el laboratorio y el uso real del producto. Permite coordinar conductores de prueba, registrar sus trayectos y recolectar feedback cualitativo y cuantitativo para validar la seguridad y utilidad de THAMIS en el día a día.

## 2. Componentes Clave
- **FieldTestManager**: Orquestador del ciclo de vida de la sesión (Inicio -> Registro -> Cierre).
- **RideAnalyzer**: Motor de análisis post-viaje que detecta fallos recurrentes y calcula el `SafetyScore` real.
- **TesterProfile**: Almacena información sobre el nivel de experiencia del motociclista y su equipamiento (casco/intercom).
- **FieldReportGenerator**: Transforma los eventos de campo en informes ejecutivos para la toma de decisiones de ingeniería.

## 3. Filosofía de "Kilómetro Útil"
Cada trayecto debe aportar datos. El sistema registra no solo los errores técnicos, sino también el feedback humano (ej. *"THAMIS interrumpió en un momento incómodo"*), vinculándolo al snapshot del mundo que provocó esa interacción.

## 4. Garantía de Seguridad
- **SafetyOverride**: El sistema permite al conductor silenciar o pausar la prueba de forma instantánea mediante un comando físico o de voz de emergencia.
- **Data Minimization**: Solo se registran metadatos de eventos; nunca el contenido de la comunicación privada del tester.
