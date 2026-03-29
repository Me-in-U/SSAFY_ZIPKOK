# ZIPKOK AI Coding Assistant Instructions

## Project Baseline

- Backend-first refactor target
- Spring Boot `4.0.2`
- Java `21`
- Build tool: `Gradle`
- Root package: `com.ssafy.house`
- Frontend remains deferred during the current backend architecture migration

## Backend Structure & Architecture

- Architecture pattern: Domain-Driven Layered Architecture aligned with the ControlCenter backend style
- Top-level backend packages are now only:
  - `com.ssafy.house.api.{ai,auth,community,house,member,news,recommend,region}`
  - `com.ssafy.house.global.{batch,common,config,exception,properties,security,util}`
- Do not create new top-level backend roots such as `restcontroller`, `model`, `Security`, or `legacy`.
- Standard structure inside each domain is a subset of:
  - `controller`
  - `service`
  - `repository`
  - `entity`
  - `dto.request`
  - `dto.response`
  - `dto.internal`
  - optional `config`
  - optional `tool`
  - optional `util`
- `dto` is a namespace package only. Do not place concrete DTO classes directly under `dto`; use `request`, `response`, or `internal`.
- `repository` contains both persistence interfaces and read/query helpers:
  - Spring Data JPA repositories stay directly under `repository` and use names like `*JpaRepository`
  - custom read/query repositories also stay directly under `repository` and use names like `*QueryRepository`
  - do not create `repository.query` subpackages again
- Persistence rule:
  - MyBatis is fully removed from the backend. Do not add `@Mapper`, `mappers/*.xml`, `legacy.dao`, `legacy.service`, or any MyBatis dependency back.
  - `member`, `community`, and `house` use Spring Data JPA plus custom `*QueryRepository` classes backed by `EntityManager` where needed.
  - `news`, `recommend`, and `region` use lightweight read-only repositories built on `JdbcClient`.
  - repository facade classes may compose `*JpaRepository`, `*QueryRepository`, and `JdbcClient` access, but services should only depend on repository boundaries.
- API route rule:
  - New endpoints use `/v1/...` only.
  - Do not add new `/api/v1/...` compatibility routes.
  - Temporary exception: AI currently still accepts `/v1/ai`, `/api/v1/ai`, and `/ai` for compatibility. Do not expand this pattern to other domains.

## Mandatory Backend Rules

- New controllers use `/v1/...` endpoints.
- New controller responses return `BaseResponse<T>`.
- External request/response DTOs live in `dto/request` and `dto/response`.
- External request/response DTOs use Java `record`.
- Internal projections, views, and mapper-facing carriers live in `dto/internal`.
- Entities and internal DTOs are never exposed directly from controllers.
- Controllers should depend on domain services.
- Services should depend on repository boundaries, not on SQL helper classes or direct `EntityManager` access.
- Prefer JPA + direct-under-`repository` custom query repositories, and use `JdbcClient` only for lightweight read-only access when that is simpler than full entity mapping.
- Keep DB schema compatible while refactoring persistence code.
- All new public classes and public methods require Korean Javadoc.
- All new controller endpoints require Swagger `@Tag` and `@Operation`.
- Configuration belongs under `global/config` or `global/security`.

## Testing Rules

- Every migrated domain change must include tests.
- Preferred test mix:
  - `@WebMvcTest` for controllers
  - unit tests for services and security helpers
  - integration tests for JPA, `EntityManager` query repository, or `JdbcClient` repository changes where practical
- `./gradlew test` must pass after each migration step.

## Instruction Maintenance

- `AGENTS.md` and `.github/copilot-instructions.md` must remain synchronized with identical content.
- Update both files immediately when backend structure or migration rules change.
