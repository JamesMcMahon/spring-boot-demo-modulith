Every prompt depends only on artifacts produced by the previous prompt(s); nothing is left floating or unused.

---

## 1. High-Level Blueprint

1. Foundation  
   1.1  Create empty Git repository, agree on GitFlow strategy, set Java 17+ toolchain  
   1.2  Generate Spring Boot skeleton (Gradle or Maven) with essential starters  
   1.3  Set up JUnit 5, AssertJ, Testcontainers (H2 is in-memory, but Testcontainers is future-safe)  
   1.4  Add conventional commit / tag templates

2. Domain — Monolith v1  
   2.1  Catalog bounded-context: Book aggregate (metadata + copies)  
   2.2  Inventory rules live inside Book (DDD aggregate)  
   2.3  Lending context: LendingTransaction aggregate (single-user)  
   2.4  Value objects: BookId, LendingStatus, DueDate  

3. Persistence  
   3.1  Spring Data JDBC repositories for Book & LendingTransaction  
   3.2  Flyway (optional) or schema.sql + data.sql for H2  
   3.3  Integration tests hitting the real DB

4. REST API  
   4.1  Controllers per context (`/catalog/books`, `/lending`)  
   4.2  DTO mapping layer (record-based)  
   4.3  RFC 7807 error handling (Spring’s `@ExceptionHandler` + `ProblemDetail`)

5. Documentation & CI hooks  
   5.1  README with Mermaid diagram, curl samples  
   5.2  GitHub Actions (or local script) for test & build

6. Modular Evolution (v2-v5)  
   6.1  Introduce `spring-modulith` dependency; carve packages into modules  
   6.2  Add domain events for inter-module communication  
   6.3  Introduce command handlers (optional CQRS flavour)  
   6.4  Final polish & documentation

---

## 2. Milestones → Tasks → Micro-Steps

### Milestone A  —  Project Skeleton
A-1  Generate Spring Boot project  
A-2  Configure build plugins, Java 17, testing libraries  
A-3  Add `/health` smoke test

### Milestone B  —  Catalog Domain
B-1  Create BookId VO  
B-2  Create Book aggregate business logic  
B-3  Unit tests for Book rules  
B-4  Spring Data JDBC mapping & repository  
B-5  Repository integration tests

### Milestone C  —  Catalog REST
C-1  DTOs (BookRequest, BookResponse)  
C-2  CatalogController (CRUD)  
C-3  Controller slice tests  
C-4  RFC 7807 error handling

### Milestone D  —  Lending Domain + API
D-1  LendingStatus, DueDate VOs  
D-2  LendingTransaction aggregate  
D-3  Business rules & tests  
D-4  Repository & tests  
D-5  LendingController (borrow / return)  
D-6  Controller tests

### Milestone E  —  Documentation & Tag v1
E-1  README scaffold + Mermaid  
E-2  Example curl commands  
E-3  Tag `v1-initial-monolith`

### Milestone F  —  Modulith Refactor
F-1  Add dependency, define modules  
F-2  Move packages, update tests  
F-3  Domain events between modules  
F-4  Tag `v2-introduce-modules`

(…continue until `v5-final-polish`)

---

## 3. Code-Generation Prompts

Copy each block to your coding LLM **one at a time**.  
Wait for green tests before moving to the next prompt.

```text
Prompt 01 – Generate Project Skeleton
Goal: Gradle build, Spring Boot 3.4.5, Java 17+, JUnit 5, Spring Data JDBC, H2, Spring Web.
Requirements:
• `build.gradle(.kts)` with explicit dependency versions
• Main class `LibraryApplication`
• Empty `application.yml`
• A single failing placeholder test that asserts true
Deliverables must compile with `./gradlew test`.

```

```text
Prompt 02 – Smoke Test & CI Friendly Setup
Goal: Replace placeholder test with real smoke test.
Requirements:
• Health endpoint using Spring Boot Actuator (add dependency)
• `LibraryApplicationTests` that hits `GET /actuator/health` via `TestRestTemplate` and expects status 200 + "UP"
• Update README with build/run instructions as first draft.
All previous tests must stay green.

```

```text
Prompt 03 – Domain Value Object: BookId
Goal: Introduce strongly-typed identifier.
Requirements:
• Immutable `BookId` record with factory method `of(UUID)` and `random()`
• Override `toString()`
• Unit tests covering equality, random generation, null guard.
No other code changes.

```

```text
Prompt 04 – Aggregate Root: Book
Goal: First business rules.
Requirements:
• `Book` aggregate with fields: `BookId id`, `String title`, `String author`, `int copiesAvailable`
• Method `addCopies(int)` (must be positive)
• Method `borrow()` (throws domain exception if none available)
• Method `returnCopy()`
• Unit tests for happy path and error path (borrow when zero).
Use package `com.example.library.catalog.domain`.

```

```text
Prompt 05 – Spring Data JDBC Repository for Book
Goal: Persist aggregates.
Requirements:
• `BookRepository extends CrudRepository<Book, UUID>`
• `BookRowMapper` (if needed) or annotations for mapping
• `schema.sql` for table `book`
• Integration test using `@DataJdbcTest` saving and loading a Book aggregate.
Migration strategy: simple `schema.sql` auto-run.

```

```text
Prompt 06 – REST DTOs & Mapper
Goal: Prepare API objects.
Requirements:
• `BookRequest` (title, author, copiesAvailable)
• `BookResponse` (id, title, author, copiesAvailable)
• `BookMapper` static util or MapStruct (choose static util for now)
• Unit tests mapping round-trip.

```

```text
Prompt 07 – CatalogController CRUD
Goal: Expose `/catalog/books`.
Requirements:
• `CatalogController` with endpoints:
  POST → create, GET (all / by id), PUT (update), DELETE
• Use `BookService` transactional component that wraps repository + domain rules
• Controller slice tests (`@WebMvcTest`) covering 201 + Location, 400 validation error, 404 not found.
Remember to register mapper bean if needed.

```

```text
Prompt 08 – RFC 7807 Error Handling
Goal: Consistent problem responses.
Requirements:
• Global `@RestControllerAdvice` converting:
  – `IllegalArgumentException` → 400  
  – `EntityNotFoundException` → 404  
• Use Spring’s `ProblemDetail` builder
• Extend existing controller tests to assert `type`, `title`, `status` properties.

```

```text
Prompt 09 – Lending Domain: Value Objects
Goal: Lending groundwork.
Requirements:
• `DueDate` value object wrapping `LocalDate`
• `LendingStatus` enum (BORROWED, RETURNED)
• Unit tests validating `DueDate::isOverdue(atDate)`.

```

```text
Prompt 10 – Aggregate Root: LendingTransaction
Goal: Business rules.
Requirements:
• Fields: `UUID id`, `BookId bookId`, `DueDate dueDate`, `LendingStatus status`
• Factory `borrow(BookId, Period lendingPeriod)` returns BORROWED instance
• Method `returnBook()` switching status (guard idempotency)
• Unit tests for borrow / return flow & idempotent return.

```

```text
Prompt 11 – Repository & Service for Lending
Goal: Persistence + domain orchestration.
Requirements:
• `LendingRepository extends CrudRepository<LendingTransaction, UUID>`
• `schema.sql` update (add table `lending`)
• `LendingService.borrow(BookId)` (14-day default), `return(UUID lendingId)`
• Integration tests across Book & LendingTransaction:
  – Borrow reduces `copiesAvailable`
  – Return increases it back.

```

```text
Prompt 12 – LendingController
Goal: Expose `/lending`.
Requirements:
• POST `/lending` body `{ "bookId": "…" }` returns lendingId
• PUT `/lending/{id}/return` returns 200
• Controller tests for success, book unavailable, id not found.
Reuse global error handler.

```

```text
Prompt 13 – Documentation & Tag v1
Goal: Ship first complete monolith.
Requirements:
• Expand README with Mermaid domain diagram and full curl examples.
• Ensure all tests green; bump version in `build.gradle` to `1.0.0`.
• Output Git commands to commit & tag `v1-initial-monolith`.
No code logic changes.

```

```text
Prompt 14 – Introduce Spring Modulith & Define Modules
Goal: Begin modularization.
Requirements:
• Add `spring-modulith-runtime` and test dependency.
• Define three modules via package structure:
  catalog, inventory (still inside Book), lending.
• Create `@ApplicationModule` per package.
• Add Modulith test verifying `ApplicationModules.of(LibraryApplication.class).verify()`.
Keep all existing tests passing.

```

```text
Prompt 15 – Domain Event: BookBorrowed
Goal: Decouple modules.
Requirements:
• Publish `BookBorrowedEvent` from `LendingService.borrow`.
• `InventoryListener` in inventory module subtracts a copy upon event (delete direct repository call in service).
• Integration test verifying event publication & handling.
Update modulith verification to ensure no cycles.

```

```text
Prompt 16 – Command Handler Pattern (Optional Enhancement)
Goal: Prepare for CQRS demo.
Requirements:
• Introduce `BorrowBookCommand` record.
• `BorrowBookHandler` component in lending module.
• Refactor controller to send command via handler.
• Unit & integration tests remain green.

```

```text
Prompt 17 – Final Polish & v5 Tag
Goal: Docs, cleanup, release.
Requirements:
• Javadoc where missing, code format.
• README: explain modular refactor path (v1→v5).
• Ensure test coverage > 85% (add tests if needed).
• Commit & tag `v5-final-polish`.

```

---

### How to Use These Prompts

1. Paste Prompt 01 into your code-generation LLM.  
2. Review & run the generated code locally; tests must pass.  
3. Commit.  
4. Proceed with Prompt 02, and so on.  

Because each prompt is self-contained and includes its own tests, you can safely revert if something breaks. These increments follow best TDD practices, avoid large leaps in complexity, and culminate with a fully wired application.