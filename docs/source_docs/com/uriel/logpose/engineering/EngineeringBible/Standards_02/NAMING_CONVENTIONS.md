# NAMING_CONVENTIONS

Engineering Bible — Standards_02
Status: OFFICIAL
Applies to: every module under `com.uriel.logpose`

------------------------------------------------------------------------------

## Purpose

Naming is architecture that the compiler cannot enforce. A name is correct
when a new developer — or a future AI continuing this project — can predict
what a file contains before opening it.

This document defines the concrete naming rules for LogPose, derived from
`MASTER_PROMPT_LOGPOSE_v2` (Naming Rules, File Organization) and from the
conventions already frozen in `logcore/tools` and `logprobe/pro`.

------------------------------------------------------------------------------

## 1. General Principles

Names must be:

- Short.
- Clear.
- Consistent with existing frozen modules.
- Predictable — the same concept must always use the same word across the
  whole project.

A name must describe a **responsibility**, never an implementation detail
and never a data type.

Forbidden, in any position (prefix, suffix, or standalone), unless part of
an already-frozen name:

- `Manager`, `ManagerManager`
- `Helper`, `HelperHelper`
- `Misc`
- `Temp`
- `New`
- `Final`
- `Test2` (or any numeric-suffix disambiguation)
- `Impl` as the only distinguishing part of a name (prefer a name that
  describes *what* the implementation does)

------------------------------------------------------------------------------

## 2. Packages

Packages describe **domains**, never technical layers alone and never
content types.

- All lowercase, no underscores, no camelCase: `logcore`, `logprobe`,
  `features.bluetooth`, `thamis.knowledge`.
- Package path mirrors the official root structure defined in
  `PROJECT_CONTEXT.md`:
  `app`, `core`, `data`, `domain`, `features`, `logcore`, `logprobe`,
  `thamis`, `engineering`.
- Never create a new root-level package. New domains are added as
  sub-packages of `features/` or as a new frozen top-level module only
  through an `ARCHITECTURE IMPROVEMENT PROPOSAL`.
- A package name must be a noun describing the domain it owns, e.g.
  `logcore.providers`, `logprobe.pro.collectors`, `thamis.hypothesis`.

------------------------------------------------------------------------------

## 3. Classes, Objects, Interfaces

- `PascalCase`.
- The name is the responsibility. Ask: "what single thing does this do?"
  and name the class that answer.
- One class → one file. The file name matches the class name exactly
  (`BluetoothProbe.kt` contains `BluetoothProbe`, nothing else).

### 3.1 Stateless utility collections — the `*Tools` pattern

LogPose has one frozen, deliberate exception to "avoid Utils": the
`*Tools` suffix, established in `logcore/tools` (`StringTools`,
`DateTools`, `CollectionTools`, etc.). This pattern is **allowed and
required** for stateless, pure-function collections scoped to a single,
narrow domain (e.g. strings, dates, files). It is not a generic escape
hatch:

- Use `*Tools` only when every function in the object is a pure,
  side-effect-free operation on the domain named by the prefix.
- Never create a catch-all `Tools` or `Utils` object with unrelated
  functions. If functions do not share a single narrow domain, split them
  into separate `*Tools` files instead.
- Prefer an extension function over adding a new function to an existing
  `*Tools` object if the function naturally belongs to a specific type.

### 3.2 Other suffixes with defined meaning

| Suffix       | Meaning                                                        |
|--------------|------------------------------------------------------------------|
| `Provider`   | Exposes a dependency or shared service through a public contract |
| `Repository` | Domain-facing contract for data access (interface lives in `domain`, implementation in `data`) |
| `Probe`      | LogProbe diagnostic capture unit for one subsystem                |
| `Collector`  | LogProbe component that gathers raw data for a `Probe`            |
| `Snapshot`   | Immutable point-in-time data model                                |
| `Dump`       | Human-readable export of a `Snapshot`                             |
| `Parser`     | Converts raw/external data into a domain model                    |
| `Builder`    | Assembles a complex object step by step                           |
| `ViewModel`  | Android Architecture Components ViewModel, feature- or screen-scoped |
| `Screen`     | Composable function/file representing one UI screen               |
| `Service`    | Android `Service` / `ForegroundService` subclass                  |
| `Logger`     | Logging facade scoped to one module (never shared globally)       |

Do not invent a new suffix if an existing one already fits. Propose new
suffixes through an `ARCHITECTURE IMPROVEMENT PROPOSAL` so they can be
added to this table.

------------------------------------------------------------------------------

## 4. Functions

- `camelCase`, verb-first: `normalizar`, `capitalizar`, `buildReport`,
  `resolveProvider`.
- A function name states exactly what it returns or does. Avoid vague
  verbs (`handle`, `process`, `doWork`) unless qualified
  (`handleBluetoothDisconnect` is acceptable, bare `handle` is not).
- Avoid boolean parameters that silently change behavior
  (`fun export(pretty: Boolean)`); prefer two named functions or a typed
  parameter (`fun exportPretty()`, `fun exportCompact()`, or
  `fun export(format: ExportFormat)`).
- Predicate functions (`Boolean` return) read as a question:
  `isValid()`, `hasPermission()`, `canRetry()`.

------------------------------------------------------------------------------

## 5. Properties and Variables

- `camelCase`.
- `val` is the default; a `var` name should make the mutability obvious
  from context (e.g. `currentState`, `retryCount`), never disguise it.
- Booleans read as a statement: `isEnabled`, `hasStarted`, not `enabled`
  alone unless it is a public configuration flag on an object
  (e.g. `ProbeLogger.enabled`, already frozen).

------------------------------------------------------------------------------

## 6. Constants

- `UPPER_SNAKE_CASE` for `const val` at file or companion-object scope:
  `TAG`, `MAX_RETRY_COUNT`.
- Group related constants in a dedicated file only when there are enough
  to justify one (`ProbeConstants.kt`); otherwise keep the constant next
  to its single usage.

------------------------------------------------------------------------------

## 7. Files

- One file, one primary responsibility. A file may contain small,
  tightly-coupled private support types (e.g. a sealed private state
  class used only by the ViewModel in that file), but never two unrelated
  public classes.
- File name = primary public declaration name, exactly, including case.
- Do not create a file merely because a class is small. Group truly
  cohesive small types in one file (e.g. a sealed `ProbeResult` hierarchy).

------------------------------------------------------------------------------

## 8. Resources (Android)

- Layout/Compose preview files: `snake_case` if XML, `PascalCase.kt` if
  Composable.
- Drawable/asset names: `snake_case`, prefixed by type where Android
  conventions require it (`ic_`, `bg_`, `anim_`).
- String resource keys: `snake_case`, scoped by feature:
  `bluetooth_connect_error`, `voice_listening_label`.

------------------------------------------------------------------------------

## 9. Tests

- Test class name = `<ClassUnderTest>Test` (e.g. `StringToolsTest`).
- Test function names describe the scenario and expectation in readable
  form: `` `normalizar collapses multiple spaces` `` (backtick-quoted
  Kotlin test names are preferred for readability).

------------------------------------------------------------------------------

## 10. Documentation Files

All documentation file names are `UPPER_SNAKE_CASE.md`:
`README.md`, `ARCHITECTURE.md`, `CHANGELOG.md`, `API.md`, `TEST_PLAN.md`,
`INTEGRATION.md`, `FREEZE.md`, `PRD_<Module>.md`. See
`DOCUMENTATION_STANDARD.md` for content requirements.

------------------------------------------------------------------------------

## 11. Language

- Code identifiers (classes, functions, packages): **English**, no
  exceptions. This keeps the codebase consistent with Android/Kotlin
  ecosystem conventions.
- Existing frozen modules (`logcore/tools`) use Spanish function names
  (`normalizar`, `capitalizar`). This is a frozen exception and must not
  be "fixed" — never rename frozen public APIs. New modules must use
  English identifiers going forward unless they are extending a frozen
  Spanish-named API for consistency within that same file.
- Documentation body text (README, ARCHITECTURE, comments explaining
  decisions) may be written in Spanish or English, matching the
  surrounding document. Do not mix languages within a single document.

------------------------------------------------------------------------------

## 12. Self-Check Before Naming Anything

Before finalizing a name, ask:

1. Does the name describe a responsibility, not a type or a layer?
2. Would a new developer guess the file's content from this name alone?
3. Does an equivalent concept already exist under a different name
   elsewhere in the project? If so, reuse that name.
4. Is this name free of the forbidden list in Section 1?
5. Does it follow the suffix table in Section 3.2, or does it need a new
   entry proposed there?

If any answer is unsatisfactory, choose a different name before writing
the file.

------------------------------------------------------------------------------
END OF DOCUMENT
