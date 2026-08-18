# KOTLIN_GUIDELINES

Engineering Bible — Standards_02
Status: OFFICIAL
Applies to: every `.kt` file under `com.uriel.logpose`
Kotlin version: 2.2.10 (see `gradle/libs.versions.toml`)

------------------------------------------------------------------------------

## Purpose

This document turns the "Kotlin Engineering Rules" section of
`MASTER_PROMPT_LOGPOSE_v2` into concrete, checkable guidelines, grounded in
the code already frozen in `logcore/tools` and `logprobe/pro`.

------------------------------------------------------------------------------

## 1. Classes

- One class, one responsibility. If a class needs "and" to describe what
  it does, split it.
- Prefer composition over inheritance. Use inheritance only for genuine
  is-a relationships (e.g. Android framework subclasses like `Service`,
  `ViewModel`).
- Keep constructors small. If a constructor needs more than ~4–5
  parameters, group related parameters into a data class or reconsider
  the class's responsibility.
- Never inject a dependency the class does not use.
- Never create a class whose only job is to hold static-like functions
  when a top-level function or an extension function would do — this is
  what the `*Tools` objects in Section 3 of `NAMING_CONVENTIONS.md` are
  for; do not duplicate that pattern with a bespoke "Manager" class.
- Avoid God Objects. A class collecting unrelated responsibilities
  (parsing + persistence + UI state, for example) must be split along its
  responsibilities before being delivered.

## 2. Interfaces

- Create an interface only when it provides real architectural value:
  multiple implementations exist or are expected, or the interface is a
  domain contract crossing a module boundary (e.g. `domain/repository`
  contracts implemented in `data/`).
- Never create an interface "just in case" for a class with a single,
  unlikely-to-change implementation.
- Every interface must have one clear responsibility, expressed by its
  name (see `NAMING_CONVENTIONS.md` Section 3).

## 3. Functions

- Prefer 10–30 lines. A function over ~50 lines is a signal to extract
  sub-steps into private functions; a function past 100 lines must be
  decomposed before delivery.
- A function answers exactly one question. If describing it needs "and",
  split it.
- Avoid boolean flags that switch behavior inside a function body; prefer
  two functions or a typed parameter (see `NAMING_CONVENTIONS.md`
  Section 4).
- Prefer expression-body functions for simple one-line logic:
  `fun trim(text: String): String = text.trim()` (as already used in
  `StringTools.kt`).
- Default parameter values are preferred over function overloads when the
  variations are simple.

## 4. Properties

- `val` by default. Use `var` only when mutation is a genuine requirement
  of the object's lifecycle (e.g. `ProbeLogger.enabled`).
- Avoid exposing mutable collections as public `var`/`val` of a mutable
  type; expose read-only views (`List`, `Map`, `Set`) and keep the
  mutable backing collection private.
- Prefer `lateinit var` only for Android lifecycle-bound properties that
  cannot be initialized in the constructor (e.g. view bindings); never as
  a substitute for proper null-handling in plain Kotlin classes.

## 5. Nullability

- Never use `!!`. If a value is guaranteed non-null by invariant, prove
  it with `require()`/`check()` at the boundary instead of asserting it
  later.
- Prefer safe calls (`?.`), the Elvis operator (`?:`), and early returns
  over nested null-checks.
- A nullable return type must document, in the function's KDoc, what
  `null` means (not found? not yet available? invalid input?).

## 6. Coroutines

- Never launch on `GlobalScope`. Every coroutine must run inside a scope
  tied to a lifecycle: `viewModelScope`, a `CoroutineScope` owned and
  cancelled by the component that created it, or a structured
  `coroutineScope { }` block.
- Never block the UI thread. Any I/O, computation-heavy, or blocking work
  must run on `Dispatchers.IO` or `Dispatchers.Default` as appropriate.
- Respect structured concurrency: a parent coroutine must not complete
  before its children; do not detach child jobs from their parent scope
  without a documented reason.
- Cancel work correctly — check `isActive`/`ensureActive()` in long loops,
  and clean up resources in `finally` blocks or `use { }`.
- `CoroutineTools.kt` and `RetryTools.kt` are the project's approved
  entry points for coroutine helpers (`kotlinx-coroutines-core`,
  `kotlinx-coroutines-android`). Reuse them instead of re-implementing
  retry/dispatcher-switching logic.

## 7. Exceptions and Error Handling

- Never swallow an exception silently (empty `catch` block is forbidden).
- Every recoverable failure must produce useful information: log it via
  the module's own `*Logger` (see `NAMING_CONVENTIONS.md` Section 3.2) or
  surface it through a typed result (`Result<T>`, a sealed `*Result`
  type such as `ProbeResult`, or a thrown domain-specific exception).
  ExceptionTools.kt in `logcore/tools` provides shared helpers for this.
- Every unrecoverable failure must fail fast — do not catch an exception
  only to continue in an inconsistent state.
- Prefer domain-specific exception/result types over throwing or catching
  generic `Exception`/`Throwable`, except at the outermost boundary
  (e.g. a top-level coroutine exception handler) where a last-resort
  catch-all is legitimate and must be logged.

## 8. Comments and KDoc

- Every public class, object, and function must have a KDoc comment
  (`/** ... */`) describing its responsibility — this is the existing,
  frozen standard in `logcore/tools` and must be followed for every new
  file.
- KDoc explains **why**, and **what the caller needs to know** (params,
  return meaning, null meaning). It does not restate the function name in
  prose.
- Do not comment obvious code (`// increment counter` above `count++` is
  forbidden).
- Do explain: non-obvious algorithms, architectural decisions embedded in
  a specific implementation choice, and any deviation from an otherwise
  expected pattern (see `ProbeLogger.kt`'s comment distinguishing itself
  from `ProbeEvent`, as a good example already in the codebase).
- Reference parameters with Kotlin's `[paramName]` KDoc syntax, as already
  used throughout `StringTools.kt`.

## 9. Immutability and Data Modeling

- Prefer `data class` for models (`domain/models`, `*Snapshot` types).
- Prefer `sealed class`/`sealed interface` for closed sets of states or
  results (e.g. `ProbeResult`).
- Favor immutable collections (`List`, `Map`, `Set`) at public API
  boundaries; use `MutableList`/etc. only as private implementation
  detail.

## 10. Style and Formatting

- Standard Kotlin official code style (4-space indentation, as used
  throughout the existing codebase).
- One top-level public declaration per file, matching the file name
  (see `NAMING_CONVENTIONS.md` Section 7).
- Imports: no wildcard imports; order is not manually enforced beyond
  what the IDE default produces, but must be free of unused imports
  before delivery (see `COMPILATION CHECKLIST` in the Master Prompt).
- No commented-out dead code in delivered files.

## 11. Dependencies

Before adding any external dependency, in order:

1. Can this be solved with plain Kotlin?
2. Can this be solved with the Android SDK?
3. Can this be solved with existing LogPose code (check `logcore/tools`
   first)?

Only if all three are "no" is an external dependency justified. External
libraries already accepted into the project (`kotlinx.coroutines`,
`org.json`, AndroidX core) may be used freely within their existing
scope; do not introduce a new dependency for something one of them
already covers.

## 12. Anti-Patterns — Never Generate

God Classes, God Managers, God ViewModels, massive Activities/Fragments,
circular dependencies, copy-pasted code, unused interfaces, premature
abstractions, unapproved reflection, `TODO`/`FIXME` comments, placeholder
or stub implementations, or pseudo-code (`"..."`, `"Implement later"`,
`"Left as exercise"`). Every delivered function must contain its real,
working implementation.

## 13. Pre-Delivery Kotlin Checklist

Before a `.kt` file is considered complete, verify:

- ✓ Single responsibility, name matches content
- ✓ No `!!`, no swallowed exceptions
- ✓ No `GlobalScope`, coroutines properly scoped
- ✓ Public API has KDoc
- ✓ `val` preferred over `var`
- ✓ No unused imports, no dead code
- ✓ No `TODO`/`FIXME`/placeholder text
- ✓ File compiles standalone against its declared dependencies

------------------------------------------------------------------------------
END OF DOCUMENT
