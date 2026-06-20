# AGENTS.md

This file provides guidance to Codex (Codex.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./gradlew build
./gradlew build -x test          # skip tests
./gradlew lambdaJar              # build Lambda-specific JAR

# Test
./gradlew clean test --no-daemon --info --stacktrace
./gradlew test --tests "sopt.org.homepage.ClassName"          # single class
./gradlew test --tests "sopt.org.homepage.ClassName.method"   # single method

# Local infrastructure
docker-compose up -d             # starts PostgreSQL for local dev
```

Tests require Docker (TestContainers spins up a PostgreSQL container).

## Architecture

**Spring Boot 3.2.3, Java 17.** All API routes are prefixed with `/v2` (servlet context path).

### Package layout (`sopt.org.homepage`)

| Package | Role |
|---|---|
| `application/` | Use-case controllers and orchestration services (admin, homepage, recruitpage, visitor) |
| `{domain}/` | Domain modules — notification, member, recruitment, review, soptstory, faq, news, etc. |
| `infrastructure/` | External concerns: AWS S3, Caffeine/Redis cache, OpenFeign clients |
| `global/` | Config beans, JWT filter chain, global exception handler, shared utilities |

### Two module styles

**Light modules** (most domains: notification, faq, news, member, part, generation, etc.)
- Flat structure: entity → repository → service → controller
- JPA entity acts as domain model — no separate domain object
- Integration tests only (no unit tests), using `IntegrationTestBase`

**Full DDD modules** (review, soptstory)
- Rich domain models with business logic separated from entities
- Unit-testable domain layer; integration tests for the full stack

### Key infrastructure

- **Database**: PostgreSQL. Flyway migrations under `classpath:db/migration`. Dev/prod use `ddl-auto: none` with Flyway enabled; test profile uses `ddl-auto: create-drop` with Flyway disabled.
- **QueryDSL**: used for complex projections and read queries (Command/Query repository split on complex modules).
- **Caching**: Caffeine by default; Redis available (Lambda profile). Switchable via `CacheType` enum.
- **External HTTP**: OpenFeign clients in `infrastructure/external/` — Playground API, Crew API, Auth service.
- **AWS**: S3 (presigned URL generation), Lambda (via `LambdaHandler` bridging API Gateway → Spring).
- **Security**: JWT validated by `JwtAuthenticationFilter` + `JwtExceptionFilter`.

### Environment / profiles

Profiles: `dev`, `prod`, `lambda-dev`, `test`. Activate via `SPRING_PROFILES_ACTIVE`.

Required environment variables (see `application-{profile}.yml` for full list):
- `DB_HOST/PORT/DATABASE/USERNAME/PASSWORD`
- `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `BUCKET_NAME`
- `JWT_*`, `ADMIN_TOKEN_SECRET`
- `*_API_URL` for Playground, Crew, Auth clients
- `REDIS_HOST`, `REDIS_PORT` (Lambda profile only)
- `SENTRY_*_DSN`

Dev defaults live in `src/main/resources/.env`.

## Testing conventions

- Base class: `IntegrationTestBase` — provides `@Transactional` rollback per test.
- DisplayName emoji convention: `✅` success path, `❌` failure path, `🔍` query behavior.
- Light modules → integration tests only. Full DDD modules → unit tests for domain + integration tests for the slice.
- See `docs/testing-guide.md` for detailed patterns.

## Deployment

CI runs on PRs (`.github/workflows/ci.pr.yml`): YAML lint → tests → build → docker build.  
CD on merge to `develop`/`main`: docker build → push to AWS ECR → deploy to EC2.  
Lambda path: `deploy-lambda-dev.yml` packages with `lambdaJar` and deploys to AWS Lambda.
