# GIT_WORKFLOW

Engineering Bible — Standards_02
Status: OFFICIAL
Applies to: every commit made to the LogPose repository

------------------------------------------------------------------------------

## Purpose

Defines how LogPose uses Git, grounded in the project's actual commit
history (`git log`) and the "GIT" / "Freeze Criteria" sections of
`MASTER_PROMPT_LOGPOSE_v2`.

------------------------------------------------------------------------------

## 1. Branching Model

The repository currently operates on a single integration branch:

- `main` — the only branch observed in the repository (`origin/main`).
  All frozen, compiling work lives here.

Because LogPose is built and maintained by a single developer (per
`PROJECT_CONTEXT.md` philosophy: "one person must be able to develop,
maintain, deploy the entire project"), the default workflow commits
directly to `main` once a module reaches a compiling, reviewed state —
mirroring the existing history.

For larger, multi-session module work that will leave the project in a
temporarily non-compiling state, use a short-lived feature branch named:

```
feature/<module-slug>
```

e.g. `feature/logcore-providers`. Merge back into `main` only once the
module compiles and passes internal review (Phase 4 of
`MODULE_TEMPLATE.md`). Never leave `main` in a non-compiling state.

## 2. Commit Message Convention

Two message shapes are both already established in project history and
both remain valid; choose based on what the commit represents.

### 2.1 Version milestone commits (primary convention)

Used when a commit represents completing or advancing a module/version,
matching the majority of existing history:

```
vMAJOR.MINOR.PATCH - <Short description>
```

Examples already in the repository:

```
v0.6.0 - Preparación del módulo LogCore Providers
v0.6.2 - Modular knowledge architecture
v0.7.1 - Complete LogCore architecture foundation
v0.6.1 - Engineering Bible reorganizada y estructura del proyecto consolidada
```

The description is a short, specific statement of what changed — not a
generic phrase. Language (Spanish or English) may match the surrounding
recent history; do not mix languages within the same message.

### 2.2 Conventional-style commits (scoped changes)

Used for a discrete, scoped change that does not itself represent a
version milestone (already present in history: `c23e9f8`):

```
<type>(<scope>): <description>
```

- `type`: `feat`, `fix`, `docs`, `refactor`, `test`, `chore`.
- `scope`: the module or package affected, e.g. `logcore`, `logprobe`,
  `thamis`, `engineering`.

Example already in the repository:

```
feat(logcore,logprobe): integrate LogCore Tools and LogProbe
docs(adr): establish LogPose foundational architecture decisions
```

### 2.3 Rule of thumb

If the commit completes, freezes, or advances a module's version →
use the `vX.Y.Z - Description` form. If it is a smaller, scoped change
within ongoing work on a module not yet at a milestone → use the
`type(scope): description` form. Never invent a third format.

## 3. Versioning

- Follow `MAJOR.MINOR.PATCH`.
- `PATCH` — internal fixes, documentation-only changes, small
  non-breaking adjustments within a module.
- `MINOR` — a new module, feature, or backward-compatible capability
  added.
- `MAJOR` — reserved for changes to the official root architecture
  (`PROJECT_CONTEXT.md`'s "OFFICIAL ARCHITECTURE" section) — expected to
  be extremely rare, since that structure is frozen.
- The version suggested in a delivery must match the module's actual
  scope of change; do not inflate or deflate it.

## 4. What Belongs in One Commit

- One commit = one coherent unit of work (one module phase, one fix, one
  documentation update). Do not bundle unrelated changes across modules
  into a single commit.
- A module delivery that spans many files (source + docs) is committed
  together once it reaches a compiling, reviewed state — do not commit
  partially-implemented files.

## 5. Required Commit Footer (per Master Prompt "GIT" section)

At the end of every module delivery, the suggested Git block must include:

```
Version: vX.Y.Z

git add .
git commit -m "vX.Y.Z - <Description>"
git push origin main
```

If a feature branch was used:

```
git add .
git commit -m "vX.Y.Z - <Description>"
git push origin feature/<module-slug>
# merge into main once compiling and reviewed
```

## 6. Frozen Modules

Modules marked `Frozen` in `PROJECT_STATE.md` (currently: `LogProbe`,
`LogCore Tools`) must never be modified by a commit unless the task
explicitly requests changing that module. If a change to a frozen module
seems necessary as a side effect of other work, stop and raise it as an
`ARCHITECTURE IMPROVEMENT PROPOSAL` (see `MASTER_PROMPT_LOGPOSE_v2`,
"Limits of Autonomy") instead of committing the change silently.

## 7. Pre-Commit Checklist

Before suggesting or making a commit, verify:

- ✓ Project compiles
- ✓ No `TODO`/`FIXME`/placeholder content included
- ✓ Documentation (`README.md`/`ARCHITECTURE.md`/`CHANGELOG.md`) updated
  to match the code in this commit
- ✓ `CHANGELOG.md` of the affected module has a new entry for this
  version
- ✓ Commit message follows Section 2
- ✓ No frozen module was modified without explicit instruction
- ✓ `PROJECT_STATE.md` updated if this commit changes overall project
  progress, current module, or completed/pending module lists

## 8. Updating PROJECT_STATE.md

`engineering/00_Project/PROJECT_STATE.md` reflects the live status of the
project (version, current module, completed/pending modules, progress
percentages). Any commit that finishes, freezes, or starts a module must
update this file in the same commit — it must never drift from reality.

------------------------------------------------------------------------------
END OF DOCUMENT
