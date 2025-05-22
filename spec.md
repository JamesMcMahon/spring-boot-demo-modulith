# Developer Specification for Spring Modulith Demo Project

## Project Overview

The goal of this project is to create a demo application that illustrates the evolution of a monolithic application into a modular design using Spring Modulith. The audience is technical, with a basic understanding of Spring, and the project aims to educate them on the benefits of modular design and Spring Modulith.

## Architecture and Design

### Initial Monolithic Design

1. **Stage 1 (v2 - Functional Groups):**
   - Organize the codebase into functional groups:
     - `controllers`: Contains all controller classes.
     - `models`: Contains all model classes.
     - `dataaccess`: Contains all repository and data access classes.

2. **Stage 2 (v3 - Feature Groups):**
   - Transition to feature groups or bounded contexts:
     - `catalog`: Manages book metadata and search functionality.
     - `inventory`: Tracks physical book copies and their statuses.
     - `lending`: Handles borrowing, returning, and reservations.

### Modular Design

- **Stage 3:** Evolve into a modular design using Spring Modulith, maintaining the same functionality but restructuring the codebase for better modularity.

## Domain Design

- **Catalog Context:**
  - Manages book metadata: title, author, ISBN.
  - Emits `BookRegistered` event.

- **Inventory Context:**
  - Tracks physical book copies (`BookItem`).
  - Consumes `BookRegistered` event.
  - Emits `BookItemAdded`, `BookItemRemoved` events.

- **Lending Context:**
  - Handles borrowing, returning, and reservations.
  - Emits `LoanCreated`, `LoanReturned`, `ReservationPlaced`, `LoanOverdue` events.

## Data Handling

- Use direct method invocations for interactions between contexts.
- Ensure data consistency and integrity across contexts.

## Error Handling

- Implement standard error handling practices in Spring, such as using `@ExceptionHandler` for controller advice.
- Ensure meaningful error messages are returned to the client.

## Testing Plan

- **Unit Tests:** Cover individual components and methods within each context.
- **Integration Tests:** Test interactions between contexts and ensure events are correctly emitted and consumed.
- **End-to-End Tests:** Validate the complete workflow from book registration to lending and returning.

## Documentation

- **README.md:** Provide a comprehensive guide to the project, including:
  - Overview of the project and its goals.
  - Step-by-step guide on the evolution process with references to specific commits.
  - Diagrams using Mermaid to visualize architecture changes.
  - Explanations of key concepts like bounded contexts and domain events.

## Versioning and Tags

- Use sequential Git tags with brief descriptions to mark different stages of the project's evolution:
  - `v1-functional-groups`
  - `v2-feature-groups`
  - `v3-modular-design`
