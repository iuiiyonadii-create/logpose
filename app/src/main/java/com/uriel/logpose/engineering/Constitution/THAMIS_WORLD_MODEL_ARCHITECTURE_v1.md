# THAMIS World Model Architecture v1.0

## 1. Visión General
El World Model es el corazón informativo de THAMIS. Reemplaza al antiguo WorldState, ofreciendo una estructura inmutable, auditable y basada en snapshots para garantizar la consistencia en la toma de decisiones.

## 2. Componentes Clave
- **WorldSnapshot**: Una foto completa y congelada del estado del usuario, vehículo y entorno en un instante dado.
- **WorldModelEngine**: El único orquestador con capacidad de mutar el estado mediante funciones reductoras puras.
- **WorldHistory**: Almacenamiento circular de los últimos 1000 snapshots.
- **WorldAudit**: Registro forense de quién modificó el mundo y cuánto tardó.

## 3. Flujo de Datos
1. Los dominios (Android, Sensores, Apps) informan cambios.
2. El `WorldModelEngine` recibe la petición y aplica un reducer.
3. Se genera un nuevo `WorldSnapshot` con un UUID único.
4. El snapshot se guarda en el historial y se pone a disposición del cerebro cognitivo.

## 4. Estándares Cumplidos
- 100% Kotlin Puro (JVM compatible).
- Cero dependencias Android.
- Inmutabilidad estricta.
