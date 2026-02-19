# Flash Cards

A backend API for a flash card application focused on learning foreign language words.

Users create card sets, add cards with front/back text, run learning sessions, 
and track their progress via per-card statistics (attempts, success streaks, learning state).

## Tech stack

- Kotlin 2.3, JVM 21
- Ktor (HTTP server)
- Exposed (SQL ORM)
- PostgreSQL 17
- Flyway (database migrations)
- Testcontainers (integration tests)

## Prerequisites

- JDK 21+
- Docker (required for running tests via Testcontainers and for local PostgreSQL)

## Building & Running

```bash
# Run tests
./gradlew test

# Build everything (compile + test)
./gradlew build

# Build a fat JAR with all dependencies
./gradlew buildFatJar

# Build docker image
docker build -t flash-cards .

# Run the server (development)
docker compose up
./gradlew run
```

The server starts on `http://localhost:8080` by default.

### Database configuration

The app expects a PostgreSQL database. Configure via environment variables:

| Variable            | Default                                |
|---------------------|----------------------------------------|
| `DATABASE_URL`      | `jdbc:postgresql://localhost:5432/flashcards` |
| `DATABASE_USER`     | `flashcards`                           |
| `DATABASE_PASSWORD` | `flashcards`                           |

Flyway runs migrations automatically on startup.

## API overview

All endpoints under `/api` (except `POST /api/users`) require an `X-User-Id` header containing the user's external ID. This header is expected to be set by an upstream auth proxy (e.g. AWS Cognito).

| Method | Endpoint                                   | Description                  |
|--------|--------------------------------------------|------------------------------|
| POST   | `/api/users`                               | Register a new user          |
| GET    | `/api/card-sets`                           | List user's card sets        |
| POST   | `/api/card-sets`                           | Create a card set            |
| GET    | `/api/card-sets/{id}`                      | Get a card set               |
| PUT    | `/api/card-sets/{id}`                      | Update a card set            |
| DELETE | `/api/card-sets/{id}`                      | Delete a card set            |
| GET    | `/api/card-sets/{cardSetId}/cards`         | List cards in a set          |
| POST   | `/api/card-sets/{cardSetId}/cards`         | Create a card                |
| GET    | `/api/card-sets/{cardSetId}/cards/{cardId}`| Get a card                   |
| PUT    | `/api/card-sets/{cardSetId}/cards/{cardId}`| Update a card                |
| DELETE | `/api/card-sets/{cardSetId}/cards/{cardId}`| Delete a card                |
| POST   | `/api/sessions`                            | Save learning session logs   |
| GET    | `/api/statistic`                           | Get per-card statistics      |
