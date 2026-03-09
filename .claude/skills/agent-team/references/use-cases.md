# Agent Team Use Case Patterns

## 1. Parallel Code Review

Reviewers split by concern: security, performance, test coverage.

```text
Create an agent team to review PR #142. Spawn three reviewers:
- One focused on security implications
- One checking performance impact
- One validating test coverage
Have them each review and report findings.
```

## 2. Competing Hypothesis Debugging

Multiple investigators test different theories in parallel, debate findings.

```text
Users report the app exits after one message instead of staying connected.
Spawn 5 agent teammates to investigate different hypotheses. Have them talk to
each other to try to disprove each other's theories, like a scientific
debate. Update the findings doc with whatever consensus emerges.
```

## 3. Multi-Angle Research & Design

Explore a problem from UX, architecture, and devil's advocate perspectives simultaneously.

```text
I'm designing a CLI tool that helps developers track TODO comments across
their codebase. Create an agent team to explore this from different angles: one
teammate on UX, one on technical architecture, one playing devil's advocate.
```

## 4. Cross-Layer Feature Implementation

Split frontend, backend, and test work across teammates with file ownership.

```text
Create an agent team with 3 teammates:
- Frontend: owns src/components/ and src/pages/
- Backend: owns src/api/ and src/services/
- Tests: owns src/__tests__/
Implement the user profile feature across all layers.
```

## 5. Large-Scale Refactoring

Each teammate owns a module, refactors independently.

```text
Create a team with 4 teammates to refactor these modules in parallel:
- auth module
- payment module
- notification module
- user module
Use Sonnet for each teammate. Ensure no file conflicts.
```

## 6. Documentation Sprint

Teammates document different parts of the codebase simultaneously.

```text
Create a team to document the API. Assign one teammate per domain:
- Room API endpoints
- Participant API endpoints
- TimeBlock API endpoints
Have them generate REST Docs and update the AsciiDoc files.
```
