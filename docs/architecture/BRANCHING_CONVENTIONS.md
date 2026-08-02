# Branching Conventions

We follow **Trunk-Based Development**.

## Branch Naming
- `main`: Protected production-ready branch.
- `feature/lab-<phase>-<description>`: Feature branches (e.g. `feature/lab-p0-contracts`).
- `fix/lab-<description>`: Bugfix branches.

## Workflow
1. Create short-lived branch from `main`.
2. Implement changes with unit tests.
3. Open PR to `main` with completed checklist.
4. Pass CI checks + 1 approval ➔ Merge.
