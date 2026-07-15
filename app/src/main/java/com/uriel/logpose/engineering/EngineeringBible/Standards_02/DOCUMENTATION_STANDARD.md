# DOCUMENTATION_STANDARD

Engineering Bible — Standards_02
Status: OFFICIAL
Applies to: every module's documentation set

------------------------------------------------------------------------------

## Purpose

Per `MASTER_PROMPT_LOGPOSE_v2` ("Documentation Rules", "Documentation
Quality"): documentation is part of the module, not an afterthought.
Missing documentation means the module is incomplete. This document
defines exactly what each required file must contain and how it must be
written, grounded in the existing `logcore/tools/README.md` as the
reference example already in the codebase.

------------------------------------------------------------------------------

## 1. Mandatory Files Per Module

Every module (e.g. `logcore/providers`, `features/bluetooth`) must contain,
at minimum:

- `README.md`
- `ARCHITECTURE.md`
- `CHANGELOG.md`

When applicable to the module's nature:

- `API.md` — for modules exposing a public contract consumed by other
  modules.
- `TEST_PLAN.md` — for modules with non-trivial testing strategy.
- `INTEGRATION.md` — for modules that other modules must wire in
  (dependency injection, initialization order, required setup).
- `FREEZE.md` — written at the moment a module is frozen (see Section 6).

A module without these files is **not finished**, regardless of how
complete its code is.

------------------------------------------------------------------------------

## 2. README.md

The entry point for anyone opening the module for the first time. Must
contain, in order:

1. **Title** — `# LogPose · <module/path>` (matches the existing
   `logcore/tools` pattern: `# LogPose · logcore/tools`).
2. **Module identity** — module path and full package
   (`Módulo: \`logcore/tools\`` / `Package: \`com.uriel.logpose.logcore.tools\``).
3. **One-paragraph description** — what the module is for and, just as
   importantly, what depends on it and what it must never depend on
   (mirrors the existing README: "No depende de UI, Activities,
   Fragments ni Compose").
4. **Structure** — a file/folder listing, either as a tree or a compact
   table, one line per file with enough context to know its purpose at a
   glance.
5. **Dependencies** — every external dependency actually used, with a
   justification for each per the "Dependency Management" rule (why
   Kotlin/Android SDK/existing LogPose code was not enough). If the
   module is dependency-free, state that explicitly.
6. **Documented assumptions** — any assumption made about another module
   that could not be verified at write time (see `logcore/tools/README.md`
   Section "Supuestos documentados" for the reference format: assumption,
   why it was necessary, what happens if it turns out to be wrong).

## 3. ARCHITECTURE.md

Explains **why**, not just how. Per the Master Prompt's Documentation
Quality rule, every architectural decision recorded here must cover:

1. **Problem** — what needed solving.
2. **Decision** — what was built.
3. **Reason** — why this approach over the alternatives.
4. **Benefits** — what this buys the project.
5. **Tradeoffs** — what was given up.
6. **Future impact** — how this affects modules built later.

In addition, `ARCHITECTURE.md` must state:

- Where the module sits in the dependency graph (what it depends on, what
  is forbidden to depend on it directly — e.g. LogCore rules: "must never
  know Bluetooth/Voice/Music/Automation/THAMIS internals").
- Public contracts exposed (types, interfaces) versus internal
  implementation kept private.
- Any `ARCHITECTURE IMPROVEMENT PROPOSAL` raised during development that
  was deferred rather than implemented (see `MASTER_PROMPT_LOGPOSE_v2`,
  "Limits of Autonomy").

## 4. CHANGELOG.md

- One entry per version, newest first.
- Version numbers follow the project's existing convention observed in
  git history: `vMAJOR.MINOR.PATCH`.
- Each entry: version, one-line summary, then a short bullet list of what
  changed. Keep entries factual and specific — "Added X provider",
  "Fixed circular dependency between Y and Z" — never vague
  ("misc improvements").
- The CHANGELOG is updated in the same delivery that introduces the
  change; it is never backfilled later.

## 5. API.md (when applicable)

Required for any module exposing a contract other modules consume
(most `logcore/`, all `domain/repository` contracts, cross-feature-safe
public types).

Must document, per public symbol:

- Signature.
- Purpose (one line).
- Parameters and their meaning, including what `null` means where
  relevant (ties to `KOTLIN_GUIDELINES.md` Section 5).
- Return value meaning.
- Thrown exceptions / failure modes, and how callers are expected to
  handle them.
- Thread-safety notes if the type is used across coroutines.

## 6. FREEZE.md (when applicable)

Written only when a module reaches the Freeze Criteria defined in
`MASTER_PROMPT_LOGPOSE_v2`. Must confirm, explicitly, each criterion:

```
✓ Code completed
✓ Documentation completed
✓ Architecture documented
✓ Integrated
✓ Compiles
✓ Reviewed
✓ No known defects
```

Plus: the frozen version number, the date, and an explicit statement of
what "frozen" means for that module going forward (e.g. "Never modify
unless explicitly requested" — as already applied to `logprobe`).

## 7. TEST_PLAN.md / INTEGRATION.md (when applicable)

- `TEST_PLAN.md`: what is tested, at what level (unit vs. instrumented),
  and what is explicitly out of scope and why.
- `INTEGRATION.md`: exact steps another developer (or AI) must follow to
  wire this module into the rest of LogPose — initialization order,
  required DI registration, configuration flags.

------------------------------------------------------------------------------

## 8. Writing Rules (All Documents)

- State **why**, not only **how** — a document that only restates the
  code in prose has failed its purpose.
- Be specific. Prefer concrete numbers, file names, and package paths
  over generic descriptions.
- Do not document known issues as "future work" and move on — per the
  Master Prompt, an identified issue must be fixed before delivery, not
  logged as a TODO. Documentation records decisions and completed work,
  not a backlog.
- Language: match the surrounding document set. LogPose's existing
  architecture documentation (`ENGINEERING.md`, `logcore/tools/README.md`)
  is written in Spanish; new module documentation may follow that
  convention or use English, but must be internally consistent — never
  mix languages within a single file.
- No `TODO`, `FIXME`, "coming soon", or placeholder sections. If a
  section does not apply, omit it or state explicitly why it does not
  apply — do not leave it empty with a promise to fill it in later.

------------------------------------------------------------------------------

## 9. Pre-Delivery Documentation Checklist

- ✓ README.md, ARCHITECTURE.md, CHANGELOG.md present
- ✓ API.md / TEST_PLAN.md / INTEGRATION.md present where applicable
- ✓ Every architectural decision explains problem/decision/reason/
  benefits/tradeoffs/future impact
- ✓ Dependencies justified per the Dependency Management rule
- ✓ Assumptions documented explicitly
- ✓ No TODO/FIXME/placeholder text anywhere
- ✓ Documentation matches the delivered implementation exactly

------------------------------------------------------------------------------
END OF DOCUMENT
