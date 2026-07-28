# LogPose Code Style

We follow the [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html).

## Specific Rules
- **Naming:** Use CamelCase for classes and camelCase for functions/variables.
- **Packages:** All code must live under `com.uriel.logpose`.
- **Comments:** Use KDoc for public APIs and complex logic.
- **Architecture:** Maintain strict separation between layers (Presentation, Domain, Data).
- **Safety First:** Any logic involving audio or privacy must be validated by the `SafetyEngine` or `SecurityManager`.
