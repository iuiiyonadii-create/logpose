# ADR-001: Engineering Foundation & Architecture Standards

## Context
THAMIS Lab OS requires a resilient, multi-year engineering foundation with explicit typing, functional error handling, isolated logging, and zero-framework domain logic.

## Decision
1. **Explicit API Mode**: Enabled on all Kotlin core modules (`explicitApi()`). All public symbols must explicitly declare visibility and return types.
2. **Functional Error Handling**: Use `LabResult<T>` sealed interface instead of throwing exceptions or returning nulls for domain logic errors.
3. **Clean Architecture**: `core:common` and `core:contracts` have zero dependencies on Android SDK.
4. **Trunk-Based Development**: Short-lived feature branches (`feature/lab-*`) merged into `main` via PRs with mandatory CI checks.

## Consequences
- High code legibility and maintainability.
- Fast JVM test execution (<10ms).
- Zero pollution of Android APIs in cognitive logic.
