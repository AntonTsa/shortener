# URL Shortener

## How to Run After Cloning from GitHub

1. Copy the environment template:
```bash
cp .env.example .env
```
2. Fill in your secrets in `.env`:
```text
DB_USERNAME=your_db_user
DB_PASSWORD=your_db_password
JWT_SECRET=your_very_secret_key
```
3. Start the local database:
```bash
docker compose -f docker-compose.yml -f docker-compose.dev.yml --env-file .env up -d
```
4. Run the application via `AppLauncher` in IntelliJ IDEA.

## Running in IntelliJ IDEA

The application entry point is `com.anton.tsarenko.shortener.AppLauncher`.

1. Open `AppLauncher` in IntelliJ.
2. Run `AppLauncher.main()`.
3. Variables from local `.env` are loaded automatically on startup.
4. If needed, you can still define variables in Run Configuration; they have priority over `.env`.

## Environment Variables

Required variables for local run:

- `DB_USERNAME` - PostgreSQL username.
- `DB_PASSWORD` - PostgreSQL password.
- `JWT_SECRET` - secret key used to sign JWT tokens.

Template file: `.env.example`.

`.env` must never be committed to the repository (it is already listed in `.gitignore`).

## Local Database (dev)

For local development, start PostgreSQL with Docker Compose:

```bash
docker compose -f docker-compose.yml -f docker-compose.dev.yml --env-file .env up -d
```

Minimum `.env` content:

```text
DB_USERNAME=your_db_user
DB_PASSWORD=your_db_password
JWT_SECRET=your_very_secret_key
```

## Tests

Integration tests use Testcontainers:
https://java.testcontainers.org/

During `./gradlew test`, a PostgreSQL container for tests is started automatically. Docker must be running.
