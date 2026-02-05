# Contributing

This repository uses a PR-based workflow.
Direct pushes to `main` are disabled.

All changes are merged into `main` via **Squash merge**.

---

## Workflow
1. Create a branch from `main`
2. Make changes with small, focused commits
3. Open a Pull Request to `main`

---

## Branch naming
Use one of the following prefixes:

- `feat/<topic>` — new functionality
- `fix/<topic>` — bug fixes
- `docs/<topic>` — documentation only
- `chore/<topic>` — tooling, build, or maintenance

Examples:
- `feat/checkout`
- `fix/cart-quantity-validation`
- `docs/architecture-overview`
- `chore/add-checkstyle`

---

## Commit messages
Commits inside feature branches may be granular.
The final commit message in `main` is defined by the Pull Request title.

We use a lightweight Conventional Commits style:

- `feat: ...`
- `fix: ...`
- `docs: ...`
- `test: ...`
- `refactor: ...`
- `chore: ...`

Examples:
- `feat: implement checkout use case`
- `docs: add architecture overview`
- `chore: configure CI workflow`

---

## Pull Requests
PRs should be clear, imperative, and follow the commit message style

Examples:
- `feat: checkout endpoint`
- `docs: add ADR template`
- `chore: add checkstyle configuration`

**PR description format**
- **What:** what was changed
- **Why:** why the change was needed (link to BACKLOG or ADR if relevant)
- **How to verify:** steps to test locally

---

## Quality gates
Before opening a PR, ensure:
- `./gradlew clean build` passes
- All tests are green

Additional checks (e.g. static analysis) are introduced only if they add clear value.