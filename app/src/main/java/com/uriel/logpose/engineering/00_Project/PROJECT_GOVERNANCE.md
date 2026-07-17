# PROJECT_GOVERNANCE

Version: 1.0
Status: OFFICIAL
Authority: Highest

---

# Purpose

This document defines how LogPose is governed.

Every architectural, technical and organizational decision must comply with this document.

If any other document conflicts with this one, this document takes precedence.

---

# Document Precedence

The official order of authority is:

1. PROJECT_GOVERNANCE.md
2. MASTER_PROMPT_LOGPOSE.md
3. AI_DEVELOPMENT_STANDARD.md
4. ROLE_DEFINITIONS.md
5. DECISION_PROCESS.md
6. VERSION_POLICY.md
7. PROJECT_CONTEXT.md
8. PROJECT_BOOTSTRAP.md
9. PROJECT_STATE.md
10. Module Documentation
11. Sprint Documents

No lower-level document may contradict a higher-level document.

---

# Roles

## Project Owner

Defines product vision.

Approves priorities.

Accepts completed work.

---

## CTO / Architect

Owns the architecture.

Approves architectural changes.

Defines engineering standards.

Creates Sprint specifications.

Reviews integrations.

---

## Builder

Implements approved Sprint work.

Must follow the architecture.

Must not redesign the project.

Must not introduce incompatible changes.

---

## Reviewer

Audits code quality.

Detects regressions.

Reviews architecture compliance.

Suggests improvements.

---

## Documentation

Maintains technical documentation.

Keeps documentation synchronized with implementation.

Never changes architecture.

---

# Architectural Authority

Only the CTO / Architect may approve:

- package renames
- public API changes
- architecture redesign
- module replacement
- dependency replacement
- project-wide refactors

---

# Builder Authority

The Builder may:

- fix bugs
- implement approved modules
- add tests
- improve internal implementation
- optimize performance
- extend existing classes

The Builder may not change the architecture without approval.

---

# Engineering Principles

Architecture before implementation.

Compatibility before refactoring.

Documentation before development.

Quality before speed.

Simplicity before complexity.

---

# Backward Compatibility

Working functionality must be preserved.

Stable APIs must remain compatible unless explicitly approved.

---

# Decision Process

Every important engineering decision must be documented.

Architectural changes require justification.

---

# Sprint Rule

Every Sprint must:

- define objectives
- define scope
- define risks
- define acceptance criteria
- define deliverables

No Sprint begins without a written specification.

---

# Final Principle

LogPose is built for long-term maintainability.

Every decision must improve the project without compromising stability.