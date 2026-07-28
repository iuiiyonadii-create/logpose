# THAMIS LAB - ESTRATEGIA DE CONTROL DE VERSIONES

## 1. ESTRUCTURA DE RAMAS (Git Flow Adaptado)
- **main**: Versiones estables y certificadas por el RIE.
- **develop**: Rama de integración continua.
- **feature/**: Nuevas capacidades (ej: `feature/voice-training`).
- **experiment/**: Investigación de bajo riesgo (ej: `experiment/new-audio-model`).
- **bugfix/**: Correcciones detectadas por el RCE.
- **hotfix/**: Problemas críticos de seguridad.

## 2. ESTÁNDAR DE COMMITS (Conventional Commits)
Formato: `tipo: descripción`
- `feat`: Nueva funcionalidad.
- `fix`: Corrección de error.
- `docs`: Cambios en documentación.
- `refactor`: Mejora de código sin cambiar funcionalidad.
- `perf`: Optimización de rendimiento.
- `test`: Adición o corrección de pruebas.

## 3. VERSIONADO (SemVer)
Formato: `MAJOR.MINOR.PATCH` (Ej: v1.2.0)
- **MAJOR**: Cambios arquitectónicos o rupturas.
- **MINOR**: Nuevos módulos o motores.
- **PATCH**: Correcciones y ajustes menores.

## 4. PROCESO DE CODE REVIEW
1. **Pull Request** a `develop`.
2. **Validación Automática**: THAMIS ejecuta el Smoke Test Suite.
3. **Review Humana**: Validación de ADR y cumplimiento de Clean Architecture.
4. **Merge**: Con el sello de aprobación de TADE.
