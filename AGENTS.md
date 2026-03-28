# ZIPKOK AI Coding Assistant Instructions

## Project Baseline

- Backend-first refactor target
- Spring Boot `4.0.2`
- Java `21`
- Build tool: `Gradle`
- Root package: `com.ssafy.house`
- Frontend remains deferred during the current backend architecture migration

## Target Backend Architecture

- New code lives under:
  - `com.ssafy.house.api.{auth,member,region,house,recommend,community,news,ai}`
  - `com.ssafy.house.global.{common,exception,config,security,properties,util,batch}`
- Legacy read-only areas during migration:
  - `com.ssafy.house.restcontroller`
  - `com.ssafy.house.model.dao`
  - `com.ssafy.house.model.dto`
  - `com.ssafy.house.model.service`
  - `com.ssafy.house.Security`

## Mandatory Backend Rules

- New controllers use `/v1/...` endpoints.
- New controller responses return `BaseResponse<T>`.
- External request/response DTOs live in `dto/request` and `dto/response`.
- External request/response DTOs use Java `record`.
- Entities are never exposed directly from controllers.
- All new public classes and public methods require Korean Javadoc.
- All new controller endpoints require Swagger `@Tag` and `@Operation`.
- Prefer JPA + custom query repository for migrated domains.
- Keep DB schema compatible while migrating away from MyBatis.

## Testing Rules

- Every migrated domain change must include tests.
- Preferred test mix:
  - `@WebMvcTest` for controllers
  - unit tests for services and security helpers
  - integration tests for JPA/custom repository where practical
- `./gradlew test` must pass after each migration step.
