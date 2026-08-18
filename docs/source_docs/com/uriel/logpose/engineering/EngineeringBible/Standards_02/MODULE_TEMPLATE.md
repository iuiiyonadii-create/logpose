# MODULE_TEMPLATE

Engineering Bible — Standards_02
Status: OFFICIAL
Applies to: creation of any new LogPose module

------------------------------------------------------------------------------

## Purpose

A concrete, reusable template implementing the six-phase
`MODULE DEVELOPMENT WORKFLOW` from `MASTER_PROMPT_LOGPOSE_v2`
(Analysis → Design → Implementation → Internal Review → Documentation →
Delivery), plus the `OUTPUT FORMAT` required for every module delivery.
Use this as the checklist and skeleton for every new module or feature.

------------------------------------------------------------------------------

## 1. Before Starting — Locate or Write the PRD

Every module starts from a PRD (Product/Engineering Requirements
Document) placed under `engineering/<NN_ModuleGroup>/<SubModule>/`, e.g.
`engineering/01_LogCore/Providers/PRD_LogCore_Providers.md`.

A PRD must define:

```
# PRD

Module
<Module Name>

Status
<Draft | Approved>

------------------------------------------------------------------------------
Objective
<one paragraph>

------------------------------------------------------------------------------
Responsibilities
<bullet list>

------------------------------------------------------------------------------
Must NOT
<bullet list of explicit exclusions>

------------------------------------------------------------------------------
Architecture
The module belongs to:
<package path>

------------------------------------------------------------------------------
Deliverables
Complete source code.
README.md
ARCHITECTURE.md
CHANGELOG.md
Integration instructions.
Ready to compile.
Ready to freeze.

------------------------------------------------------------------------------
Acceptance Criteria
Compiles successfully.
No circular dependencies.
No duplicated responsibilities.
Clean Architecture respected.
Fully documented.
Ready for Git.
------------------------------------------------------------------------------
END OF DOCUMENT
```

Do not begin implementation before this PRD exists and is read completely
(Phase 1 requirement).

------------------------------------------------------------------------------

## 2. Phase 1 — Analysis (checklist)

- [ ] Read the PRD completely.
- [ ] Restate the module's objective in one sentence.
- [ ] Restate the module's single responsibility.
- [ ] List what the module must explicitly never do (from "Must NOT").
- [ ] Identify dependencies on other modules (which ones, and which
      direction — see `MASTER_PROMPT_LOGPOSE_v2` "Dependency Rules").
- [ ] Identify architectural constraints (frozen modules that cannot be
      touched, package it must live in).
- [ ] Identify risks and opportunities for improvement.

## 3. Phase 2 — Design (checklist)

- [ ] Define the package layout under the module's assigned path.
- [ ] Define classes/objects/interfaces and their single responsibility
      each, named per `NAMING_CONVENTIONS.md`.
- [ ] Define the public API surface — what other modules are allowed to
      call — and keep everything else `internal`/`private`.
- [ ] Define lifecycle: what initializes it, what owns its scope, what
      tears it down (if applicable).
- [ ] Define interactions with other modules strictly through contracts
      (interfaces), never concrete cross-module references.
- [ ] Actively eliminate unnecessary complexity — if two designs are both
      correct, choose the simpler one.

## 4. Phase 3 — Implementation (checklist)

- [ ] Implement every file completely — no stubs, no `TODO`, no
      "implement later".
- [ ] Follow `KOTLIN_GUIDELINES.md` and `ANDROID_GUIDELINES.md` for every
      file.
- [ ] Follow `NAMING_CONVENTIONS.md` for every package, class, function,
      and file name.
- [ ] Every file must compile against its declared imports.

## 5. Phase 4 — Internal Review (checklist)

Search for and fix, before moving on:

- [ ] Duplicated logic
- [ ] Unnecessary abstractions
- [ ] Excessive complexity
- [ ] Naming inconsistencies
- [ ] Architecture violations (wrong layer, wrong dependency direction)
- [ ] Dependency violations (feature-to-feature coupling, domain
      importing Android, etc.)
- [ ] Dead code, unused imports, unused classes

## 6. Phase 5 — Documentation (checklist)

Produce, per `DOCUMENTATION_STANDARD.md`:

- [ ] `README.md`
- [ ] `ARCHITECTURE.md`
- [ ] `CHANGELOG.md`
- [ ] `API.md` (if the module exposes a public contract)
- [ ] `TEST_PLAN.md` (if testing strategy is non-trivial)
- [ ] `INTEGRATION.md` (if other modules must wire it in)
- [ ] Documentation matches the implementation exactly.

## 7. Phase 6 — Delivery (checklist)

- [ ] Module is ready to integrate.
- [ ] Module is ready to compile.
- [ ] Module is ready to commit (see `GIT_WORKFLOW.md`).
- [ ] Compilation checklist (Section 8 below) passes fully.

------------------------------------------------------------------------------

## 8. Compilation Checklist (from Master Prompt, verbatim scope)

Before finishing, verify:

```
✓ Project compiles
✓ Imports are valid
✓ No unresolved references
✓ No duplicated classes
✓ No duplicated packages
✓ No missing files
✓ No empty implementations
✓ No TODOs
✓ No FIXME
✓ No commented dead code
```

------------------------------------------------------------------------------

## 9. Required Output Format for Every Module Delivery

Every delivery response must follow this exact order, per
`MASTER_PROMPT_LOGPOSE_v2` "Output Format":

1. Executive Summary
2. Implemented Improvements
3. Folder Structure
4. Source Code
5. Documentation
6. Integration Instructions
7. Suggested Git Commit
8. Known Limitations (write `"No known limitations."` if none — never
   omit the section)

Never omit a section, even if brief.

------------------------------------------------------------------------------

## 10. Standard Module Folder Skeleton

```
<module-path>/
    <Class1>.kt
    <Class2>.kt
    ...
    README.md
    ARCHITECTURE.md
    CHANGELOG.md
    API.md              (if applicable)
    TEST_PLAN.md         (if applicable)
    INTEGRATION.md       (if applicable)
```

Everything must already be in its final location — never deliver files
that require the developer to reorganize them afterward.

------------------------------------------------------------------------------

## 11. Definition of Done

A module is finished only when all of the following are true
simultaneously:

- Implementation is complete.
- Architecture is validated against `MASTER_PROMPT_LOGPOSE_v2` and this
  Standards_02 set.
- Documentation is complete and matches the code.
- Integration is documented.
- The project compiles.
- The changelog is updated.
- A git commit is suggested (see `GIT_WORKFLOW.md`).
- The module is ready to freeze (see `DOCUMENTATION_STANDARD.md`
  Section 6, `FREEZE.md`).

If any item is missing, the module is **not** finished — regardless of
how much code exists.

------------------------------------------------------------------------------
END OF DOCUMENT
