# AI_DEVELOPMENT_STANDARD

Version: 1.0
Status: OFFICIAL

---

# Purpose

This document defines how every AI must work inside the LogPose project.

It applies to ChatGPT, Claude and any future AI collaborating on the project.

---

# Primary Objective

The objective is NOT to write the most code.

The objective is to improve the project while preserving its architecture.

Every contribution must leave the project in a better state than it was found.

---

# Mandatory Principles

- Respect existing architecture.
- Preserve compatibility.
- Reuse before creating.
- Extend before replacing.
- Document before closing the Sprint.

---

# Before Every Sprint

An AI must:

1. Read the governance documents.
2. Understand the project architecture.
3. Inspect the affected modules.
4. Identify dependencies.
5. Detect risks.
6. Produce an implementation plan.

Only after that may implementation begin.

---

# Code Rules

Never:

- delete working code
- rename packages
- rename public classes
- break public APIs
- duplicate functionality
- introduce unfinished code

Always:

- reuse
- extend
- integrate
- document
- validate

---

# Documentation Rules

Every implementation must update documentation when necessary.

Minimum required:

- README
- CHANGELOG
- Integration Guide

---

# Modification Policy

Before modifying an existing file, the AI must evaluate:

- Why is the change necessary?
- Can the same result be achieved without modifying it?
- Does the change affect compatibility?
- Is there a lower-risk alternative?

---

# Validation Checklist

Before considering work complete:

- Project compiles.
- Tests pass.
- Documentation updated.
- No duplicated logic.
- No architectural violations.
- No regressions.

---

# Communication Standard

When reporting work, always include:

## Summary

## Files Created

## Files Modified

## Reason for Changes

## Risks

## Pending Work

---

# Engineering Philosophy

Think first.

Design second.

Implement third.

Validate fourth.

Document last.

Never skip any step.