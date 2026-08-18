# LEGACY ARCHIVE

Status: OFFICIAL
Owner: engineering/ (authoritative)

------------------------------------------------------------------------------

# PURPOSE

This folder preserves project history that is no longer authoritative.

Per the governance decision recorded in PROJECT_STATE.md (Architectural
Cleanup, pre-LogCore-Providers):

`engineering/` is the only authoritative source for current architecture.

Everything outside `engineering/` is legacy unless it is fully consistent
with the documents inside `engineering/`.

Nothing is deleted without a record. Obsolete material is archived here,
clearly marked, with a note on what superseded it.

------------------------------------------------------------------------------

# ARCHIVED ITEMS

## 1. LOGCORE_ORCHESTRATOR_EXPERIMENT.md

Source: `app/src/androidTest/logcore/` (22 Kotlin files, ~633 lines).

An early, unfinished design of a LogCore Action/Capability/Service pipeline.
It was placed under `src/androidTest/`, outside any Gradle-recognized source
directory (`src/androidTest/java` or `src/androidTest/kotlin`), so it never
compiled and was never part of the app.

Disposition: ARCHIVED, then deleted from `src/androidTest/`.

Value preserved: this experiment already contained a `MusicProvider`
interface, a `MusicService` that wraps it, and a `ServiceResolver` concept.
This is the earliest evidence in the repository of what "Provider" was
intended to mean in LogPose, and it directly informed the LogCore Providers
design in `engineering/01_LogCore/Providers/` (see ARCHITECTURE.md there).

## 2. ARCHITECTURE_LEGACY.md

Source: `app/src/main/java/docs/architecture/ARCHITECTURE.md` and
`app/src/main/java/docs/Knowledge/ideas/ServiceResolver.md`.

A pre-`engineering/` architecture description defining a
`UI → LogCore → Services → Providers → Android` pipeline, and a deferred
proposal for a `ServiceResolver` to decouple an Action Engine from concrete
Services ("reconsider when there are more than 8 Services").

Disposition: ARCHIVED as validated prior art. Its definition of "Provider"
(an adapter between a Service and the Android platform / a third-party
integration) is consistent with, and was adopted by, the LogCore Providers
design. It is archived rather than deleted because `engineering/` is now
the single authoritative location for this definition.

The corresponding glossary entries (`docs/Knowledge/glossary/Provider.md`,
`docs/Knowledge/glossary/LogCore.md`) were found empty and are superseded
by `engineering/01_LogCore/Providers/ARCHITECTURE.md`.

------------------------------------------------------------------------------

# DEAD CODE REMOVED (not archived verbatim — trivial content)

The following were deleted outright rather than archived, because they
carried no design information (empty package-only stub files, zero lines
of logic):

- `app/src/androidTest/logprobe/` (21 files, all 2-line package stubs
  duplicating the already-complete, frozen `logprobe/pro/` implementation).
  Same non-compiling-location issue as item 1 above.

------------------------------------------------------------------------------

# LEGACY-BY-POLICY (not physically migrated)

The following trees are declared legacy under the new governance rule but
were NOT physically moved or rewritten, because they are large, contain
product/market research unrelated to LogCore Providers, and moving them
without a dedicated review risks losing context that has nothing to do with
this module:

- `app/src/main/java/docs/` (development, research, product, testing,
  rules, and Knowledge/ sub-trees)
- `docstransferecia/` (an alternate, more detailed THAMIS specification
  set — relevant to a future THAMIS design pass, not to LogCore Providers)
- `research/` (repository root — music/voice corpus research for THAMIS)

Recommendation: triage these in a dedicated documentation pass before or
during THAMIS design. Until then, nothing in these trees may be treated as
authoritative if it conflicts with `engineering/`.

------------------------------------------------------------------------------
END OF DOCUMENT
