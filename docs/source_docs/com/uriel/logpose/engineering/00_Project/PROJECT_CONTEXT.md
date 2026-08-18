# PROJECT_CONTEXT

Version: 1.0

Status: OFFICIAL

Authority: Level 7

------------------------------------------------------------------------------

# PROJECT

Name

LogPose

------------------------------------------------------------------------------

# PURPOSE

This document defines the permanent context of the LogPose project.

It explains what LogPose is, why it exists, how it is organized and which
engineering principles must never change.

This document changes very rarely.

------------------------------------------------------------------------------

# VISION

LogPose is a modular Android platform designed to evolve into a complete
cognitive ecosystem for motorcyclists.

Its long-term objective is integrating THAMIS as the cognitive engine capable
of assisting the rider with intelligent voice interaction while minimizing
distractions.

Every engineering decision must preserve long-term maintainability.

------------------------------------------------------------------------------

# MISSION

Deliver a lightweight, reliable and safe Android platform that minimizes rider
interaction with the phone through intelligent voice-first experiences.

The application must remain fast, modular, maintainable and accessible on
low-end Android devices.

------------------------------------------------------------------------------

# PHILOSOPHY

One person should be able to:

- develop
- maintain
- understand
- deploy
- evolve

the entire project.

Before spending money...

Spend intelligence.

Local processing first.

Server second.

Architecture before speed.

Maintainability before quantity.

Never over-engineer.

Engineering exists to support the product.

The product does not exist to demonstrate engineering.

Every abstraction must solve a real problem.

------------------------------------------------------------------------------

# OFFICIAL ARCHITECTURE

The official package structure is:

com.uriel.logpose

app/

core/

data/

domain/

features/

logcore/

logprobe/

thamis/

engineering/

This structure is considered stable.

Architectural changes require approval.

------------------------------------------------------------------------------

# ARCHITECTURE FLOW

UI

↓

ViewModel

↓

Manager

↓

Repository

↓

Data Source

↓

Android APIs

Every module must respect this flow.

------------------------------------------------------------------------------

# ENGINEERING PRINCIPLES

Clean Architecture

SOLID

DRY

KISS

YAGNI

Low Coupling

High Cohesion

Composition over inheritance.

Single Responsibility.

Architecture before implementation.

------------------------------------------------------------------------------

# PROJECT OBJECTIVES

Build production-quality software.

Avoid technical debt.

Avoid unnecessary dependencies.

Design for long-term evolution.

Preserve modularity.

Document every important decision.

Maintain backward compatibility whenever possible.

------------------------------------------------------------------------------

# MODULES

Infrastructure

- LogCore
- LogProbe

Application

- Bluetooth
- Voice
- Music
- Calls
- Automation
- Driving Mode

Artificial Intelligence

- THAMIS

Future

- EOS

------------------------------------------------------------------------------

# PROJECT RULES

Never duplicate responsibilities.

Never duplicate architectures.

Never create circular dependencies.

Never modify frozen modules without approval.

Never change project philosophy.

Never introduce unnecessary abstractions.

Never create code that cannot compile.

Always preserve backward compatibility unless explicitly approved.

Every new module must integrate into the existing architecture.

Every architectural decision must be documented.

Every Sprint must improve the project.

------------------------------------------------------------------------------

# SUCCESS METRIC

Every completed module must make the project:

Cleaner.

Simpler.

More maintainable.

More scalable.

More understandable.

Better documented.

------------------------------------------------------------------------------

# DOCUMENT AUTHORITY

Authority Level: 7

This document defines the permanent context of the project.

It must remain synchronized with:

- PROJECT_GOVERNANCE.md
- PROJECT_STATE.md
- PROJECT_BOOTSTRAP.md

This document should change only when the vision or permanent architecture of
LogPose changes.

------------------------------------------------------------------------------

END OF DOCUMENT