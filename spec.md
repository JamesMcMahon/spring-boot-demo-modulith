# Spring Modulith Library Demo — Developer Specification

---

## 1. Overview

Create a demo project to illustrate evolving a monolithic Spring Boot application into a modular system using **Spring Modulith**. The audience is developers with basic Spring knowledge but new to Spring Modulith concepts.

---

## 2. Project Goals

* Build a simple **library system** monolith with three core modules:

  * **Catalog:** Manage books metadata
  * **Inventory:** Track book copies and availability
  * **Lending:** Borrowing and returning books (single-user system)
* Use **domain-driven design (DDD)** style domain model with aggregates and value objects
* Implement a **simplified relational database schema** using Spring Data JDBC and H2 (in-memory)
* Expose a **CRUD-centric REST API** with separate endpoint groups per module
* Use **RFC 7807** for standardized error responses
* Deliver **complete and passing tests** at every commit/tag following TDD principles (guiding philosophy)
* Provide a **clear README** with:

  * Mermaid diagram showing domain overview and flow
  * REST endpoint usage with example curl commands
  * Explanation of project purpose and structure
* Version the code with **descriptive commits and annotated tags** showing stepwise evolution from monolith to modular system

---

## 3. Technology Stack

* Java 17+ (compatible with Spring Boot 3.4.5)
* Spring Boot 3.4.5
* Spring Data JDBC
* H2 in-memory database
* Spring Web (REST controllers)
* JUnit 5 (or preferred testing framework)
* Spring Modulith (for modularization, added in later commits)
* Git for version control

---

## 4. Architecture & Domain Design

### 4.1 Monolith Structure (Initial)

* Single package (e.g., `com.example.library`) containing all domain and application logic
* Domain modeled using DDD patterns:

  * Aggregate Roots: `Book`, `BookCopy` (optional), `LendingTransaction`
  * Value Objects: `BookId`, `DueDate`, `LendingStatus`, etc.
  * Business logic encapsulated inside aggregates (e.g., borrow, return)
* Persistence with Spring Data JDBC, mapping domain objects to simplified relational tables

### 4.2 Simplified Relational Schema

| Table Name | Description                               | Key Columns                                 |
| ---------- | ----------------------------------------- | ------------------------------------------- |
| `book`     | Stores book metadata and available copies | `id`, `title`, `author`, `copies_available` |
| `lending`  | Lending transactions (borrowed books)     | `id`, `book_id`, `due_date`, `status`       |

* No separate `book_copy` table; inventory tracked via `copies_available` integer in `book`
* Domain model maps aggregates/VOs to these tables in the repository layer

---

## 5. REST API Specification

* Base paths separated per module:

| Module    | REST Path        | Examples                                                           |
| --------- | ---------------- | ------------------------------------------------------------------ |
| Catalog   | `/catalog/books` | CRUD operations on books                                           |
| Inventory | `/inventory`     | (Optional detailed copy management)                                |
| Lending   | `/lending`       | Borrowing (`POST /lending`), returning (`PUT /lending/{id}`), etc. |

* CRUD endpoints for each main entity (e.g., `GET`, `POST`, `PUT`, `DELETE`)
* Request and response bodies modeled to reflect domain objects clearly
* RFC 7807 compliant error responses enabled by Spring Boot

---

## 6. Error Handling

* Use Spring Boot’s built-in support for [RFC 7807 Problem Details](https://datatracker.ietf.org/doc/html/rfc7807)
* Validate inputs and domain rules, returning structured error messages with appropriate HTTP status codes:

  * `400 Bad Request` for invalid data or rule violations (e.g., borrow unavailable book)
  * `404 Not Found` for missing resources
  * Others as applicable
* Include example error responses in README

---

## 7. Testing Plan

* Follow **Test-Driven Development (TDD)** principles as guiding philosophy:

  * Write tests before implementing features or refactors
  * Ensure all tests pass at every commit/tag
* Test coverage includes:

  * Unit tests for domain aggregates and value objects
  * Integration tests for repositories and database interaction
  * Controller tests for REST endpoints and error scenarios

---

## 8. Documentation

* Provide a top-level `README.md` including:

  * Project description and goals
  * High-level Mermaid diagram illustrating domain concepts and module interactions
  * Detailed instructions for running the application
  * REST API endpoint descriptions with example `curl` commands for typical use cases
  * Explanation of error handling format
  * Guidance on how to follow the commit history to observe modular evolution

---

## 9. Versioning & Evolution Roadmap

### Tags and Commit Milestones

| Tag                    | Description                                           |
| ---------------------- | ----------------------------------------------------- |
| `v1-initial-monolith`  | Complete monolith with full domain and REST API       |
| `v2-introduce-modules` | Refactor into Spring Modulith modules with boundaries |
| `v3-add-domain-events` | Implement module communication via domain events      |
| `v4-add-commands`      | Introduce command handlers for explicit commands      |
| `v5-final-polish`      | Enhanced documentation and code cleanup               |

* Each commit contains descriptive messages reflecting scope and intent
* Tests pass in all tagged versions
* Modular concepts introduced progressively for clarity

---

## 10. Out of Scope

* Multi-user/member management (single-user system only)
* CI/CD and automated build pipelines
* Advanced inventory details beyond simple copies count
* Event and command handling in the initial monolith (added later)

---

# Ready to Start

This specification provides all requirements, architecture decisions, domain modeling approaches, REST API design, error handling, testing expectations, and versioning strategy to enable immediate developer implementation.
