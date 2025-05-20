# 📋 Project Todo Checklist  
_A comprehensive, step–by–step guide. Mark each `[ ]` as `[x]` when finished._

---

## Legend
- `[ ]` – **Open**
- `[x]` – **Done**
- `▶`  – Sub-tasks  
  (Check all sub-tasks before marking the parent task complete)

---

## 0. Preparation
- [x] Agree on Git workflow (Git Flow / trunk-based)  
  ▶ [X] Create empty Git repository  
  ▶ [X] Add `.gitignore` (Java, Gradle/Maven, IDE files)  
- [X] Configure Java toolchain (17 +) in IDE & build file  
- [X] Decide on build tool  
  ▶ [ ] Gradle  
  ▶ [X] or Maven  
- [X] Define versioning & release naming convention  

---

## 1. Project Skeleton (Milestone A)
- [X] Generate Spring Boot project skeleton  
  ▶ [X] `LibraryApplication` main class  
  ▶ [X] Build file with explicit versions  
  ▶ [X] Dependencies: Spring Web, Spring Data JDBC, H2, Test, Lombok (optional)  
- [X] Add JUnit 5 + AssertJ + Spring-Boot-Test  
- [X] Provide empty `application.yml`  
- [X] Commit initial state  

### Smoke Test
- [X] Add Spring Boot Actuator  
- [X] Write health-check test hitting `/actuator/health` (expects 200 & `"UP"`)  
- [X] Ensure CI pass (local `./gradlew test`)  

---

## 2. Catalog Domain (Milestone B)
- [ ] Value Object `BookId`  
  ▶ [ ] Static factory `of(UUID)`  
  ▶ [ ] `random()` helper  
  ▶ [ ] Null-guard & equality tests  
- [ ] Aggregate `Book`  
  ▶ [ ] Fields: id, title, author, copiesAvailable  
  ▶ [ ] `addCopies(int)` (+ve only)  
  ▶ [ ] `borrow()` (guard zero stock)  
  ▶ [ ] `returnCopy()`  
  ▶ [ ] Unit tests (happy & error paths)  
- [ ] Spring Data JDBC mapping  
  ▶ [ ] `BookRepository`  
  ▶ [ ] Table DDL in `schema.sql`  
  ▶ [ ] Integration test saving/loading a Book  

---

## 3. Catalog REST API (Milestone C)
- [ ] DTOs  
  ▶ [ ] `BookRequest`  
  ▶ [ ] `BookResponse`  
  ▶ [ ] `BookMapper` util  
  ▶ [ ] Mapper unit tests  
- [ ] `CatalogController` CRUD (`/catalog/books`)  
  ▶ [ ] POST create (201 + Location)  
  ▶ [ ] GET all / by id  
  ▶ [ ] PUT update  
  ▶ [ ] DELETE remove  
- [ ] `BookService` (transactional)  
- [ ] Controller slice tests  
  ▶ [ ] Success 200/201 paths  
  ▶ [ ] 400 validation  
  ▶ [ ] 404 not-found  

- [ ] Global RFC 7807 error handler  
  ▶ [ ] Map `IllegalArgumentException` ➜ 400  
  ▶ [ ] Map `EntityNotFoundException` ➜ 404  
  ▶ [ ] Extend tests to verify `ProblemDetail` structure  

---

## 4. Lending Domain & API (Milestone D)
- [ ] Value Objects  
  ▶ [ ] `DueDate` (wrap `LocalDate`, `isOverdue(atDate)`)  
  ▶ [ ] `LendingStatus` enum  
- [ ] Aggregate `LendingTransaction`  
  ▶ [ ] Factory `borrow(BookId, Period)`  
  ▶ [ ] `returnBook()` idempotent  
  ▶ [ ] Unit tests  
- [ ] Repositories & Persistence  
  ▶ [ ] `LendingRepository`  
  ▶ [ ] Extend `schema.sql` (`lending` table)  
  ▶ [ ] Integration tests with Book & Lending  
- [ ] `LendingService`  
  ▶ [ ] `borrow(BookId)` (default 14 days)  
  ▶ [ ] `return(UUID)`  
- [ ] `LendingController` endpoints (`/lending`)  
  ▶ [ ] POST borrow (returns lending id)  
  ▶ [ ] PUT return  
  ▶ [ ] Tests: success, unavailable book, id not found  

---

## 5. Documentation & Release v1 (Milestone E)
- [ ] Expand `README.md`  
  ▶ [ ] Project overview  
  ▶ [ ] Build & run instructions  
  ▶ [ ] Mermaid diagram of bounded contexts  
  ▶ [ ] Sample `curl` snippets  
- [ ] Verify all tests pass  
- [ ] Bump version to `1.0.0`  
- [ ] Git tag `v1-initial-monolith`  

---

## 6. Modular Evolution (Milestone F)
- [ ] Add `spring-modulith-runtime` dependency  
- [ ] Define packages as modules  
  ▶ [ ] `catalog`  
  ▶ [ ] `inventory`  
  ▶ [ ] `lending`  
- [ ] Annotate each with `@ApplicationModule`  
- [ ] Modulith verification test (no cycles)  
- [ ] Commit & tag `v2-introduce-modules`  

### Domain Events
- [ ] Create `BookBorrowedEvent`  
- [ ] Publish event from `LendingService.borrow`  
- [ ] `InventoryListener` adjusts stock on event  
- [ ] Integration test for event flow  
- [ ] Update Modulith tests  
- [ ] Tag `v3-domain-events`  

### Command Handler (optional CQRS)
- [ ] `BorrowBookCommand`  
- [ ] `BorrowBookHandler` component  
- [ ] Refactor controller to use handler  
- [ ] Adjust tests  
- [ ] Tag `v4-command-handler`  

---

## 7. Final Polish & v5
- [ ] Add/complete Javadoc & code formatting  
- [ ] Ensure test coverage ≥ 85 %  
- [ ] README: document migration path v1 ➜ v5  
- [ ] Remove dead code & TODOs  
- [ ] Final CI build green  
- [ ] Tag `v5-final-polish`  

---

## 8. CI / Dev-Ops
- [ ] GitHub Actions (or other) workflow  
  ▶ [ ] Build on push & PR  
  ▶ [ ] Run tests  
  ▶ [ ] Cache Gradle/Maven dependencies  
- [ ] Add status badge to README  
- [ ] Optionally integrate Dependabot / Renovate  

---

## 9. Stretch Goals (Future)
- [ ] Switch H2 ➜ PostgreSQL via Testcontainers in dev/test  
- [ ] Dockerfile & Docker-Compose for prod demo  
- [ ] Metrics & tracing (Micrometer + Zipkin/Jaeger)  
- [ ] Kubernetes Helm chart  
- [ ] Observability dashboard (Grafana)  

---