# ARCHITECTURE DEBT (Tracked, Not Yet Resolved)

Status: OFFICIAL
Recorded during: Architectural cleanup preceding LogCore Providers.

------------------------------------------------------------------------------

This document tracks known inconsistencies that were deliberately NOT fixed
during the pre-Providers cleanup, because resolving them would require a
product/architecture decision outside the scope of "clean up before building
LogCore Providers" — specifically, decisions belonging to the future THAMIS
design phase or to a wider `core/` refactor unrelated to Providers.

Each item includes why it was left alone and who should decide it.

------------------------------------------------------------------------------

## 1. THAMIS has two parallel, incompatible "Hypothesis" models

`thamis/decision/` (Hypothesis, Decision, DecisionEvidence, DecisionType,
DecisionReason, HypothesisEngine) and `thamis/hypothesis/` (Hypothesis,
HypothesisScore, HypothesisBuilder, HypothesisEngine) both model the same
concept — a candidate interpretation of user intent — with materially
different shapes:

- `decision.Hypothesis`: `intent`, `score: Float`, single `DecisionEvidence`.
- `hypothesis.Hypothesis`: `id`, `intent`, `evidences: List<Evidence>`,
  `score: HypothesisScore`.

Both packages also each define their own `HypothesisEngine`.

**Why not merged now:** THAMIS is 10% complete and explicitly listed as a
Pending Module in PROJECT_STATE.md. THAMIS is not merely infrastructure —
per PROJECT_CONTEXT.md it is the project's long-term cognitive engine, so
choosing its evidence/scoring model is a cognitive-architecture decision,
not a mechanical cleanup. LogCore Providers has zero dependency on either
package (LogCore must never know THAMIS internals — MASTER_PROMPT,
"LogCore Rules"), so this duplication does not block or affect the
Providers module.

**Recommendation:** resolve during THAMIS's own Phase 1 (Analysis) design
pass, with a dedicated PRD, before any further THAMIS implementation work.
Until then, treat both packages as unfrozen, both non-authoritative, and
avoid adding new code that depends on either.

------------------------------------------------------------------------------

## 2. `core/compat/core/` package nesting

`core/compat/core/{AppState, Command, Constants, DeviceClassifier,
LogPoseLogger}.kt` places a package literally named `core` inside
`core/compat`, one level below the top-level `core/` module and alongside
the unrelated top-level `logcore/` module. This is a naming inconsistency
(a `core` inside a `core`) that increases the chance of confusing `core`
with `logcore` in imports and reviews.

**Why not renamed now:** `core/engine/LogPoseEngine.kt`,
`core/commands/CommandProcessor.kt`, `core/notifications/NotificationHelper.kt`,
and `core/services/LogPoseService.kt` all depend on
`core.compat.core.*`. Renaming touches five files across the `core`
module's active runtime path (already wired to `MainActivity`/services),
which is a broader refactor than "clean up before Providers" and carries
real regression risk for functionality outside this module's scope.

**Recommendation:** rename `core/compat/core/` to `core/compat/state/`
(its actual content — AppState, Command, Constants, DeviceClassifier,
Logger — is compat/runtime state, not a second "core") in a dedicated,
reviewed pass, not bundled into an unrelated module delivery.

------------------------------------------------------------------------------

## 3. Documentation debt on already-"Frozen" modules

PROJECT_STATE.md marks LogCore Tools and LogProbe as Frozen, but neither
has the ARCHITECTURE.md / CHANGELOG.md / FREEZE.md required by
MASTER_PROMPT's Documentation Rules and Freeze Criteria. LogCore Tools has
only a README.md; LogProbe has no documentation files at all.

**Why not written now:** producing accurate ARCHITECTURE.md/CHANGELOG.md
for ~40 tool files and the full LogProbe diagnostic module, to the quality
bar MASTER_PROMPT requires, is a substantial documentation effort in its
own right and unrelated to Providers. Writing it hastily under this
delivery would itself violate the quality gate.

**Recommendation:** schedule a dedicated documentation pass for both
modules. Until it is done, they should be considered "Complete but not
formally Frozen" rather than "Frozen," to keep PROJECT_STATE.md honest.

------------------------------------------------------------------------------
END OF DOCUMENT
