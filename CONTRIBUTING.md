# Contributing

## Branching

- `feature/<name>` for new capabilities
- `fix/<name>` for bug fixes
- `docs/<name>` for documentation changes

## Commit conventions

Use Conventional Commits, for example:

- `feat: add project CRUD API`
- `fix: correct session logout flow`
- `docs: update architecture notes`
- `test: add auth integration coverage`

## Quality gates

Before opening a PR:

- `cd backend && mvn verify`
- `cd frontend && npm ci && npm run build`

Do not commit secrets. Never add `.env`, private keys, DB credentials, or certificates to the repository.
