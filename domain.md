# 📚 Library System – Domain Summary (DDD & Modular Design)

## 🧭 Purpose

A modular system that allows patrons to search for books, check availability, borrow, and return physical
copies—designed using **Domain-Driven Design (DDD)**, **bounded contexts**, and intended to evolve from a **modular
monolith** to **microservices**.

---

## 🧩 Bounded Contexts

### 1. Catalog

- Manages **book metadata**: title, author, ISBN.
- Used for search and display.
- Stable, read-mostly context.
- Emits: `BookRegistered`

### 2. Inventory

- Tracks **physical book copies** (`BookItem`).
- Manages statuses: available, checked out, lost, etc.
- Consumes: `BookRegistered`
- Emits: `BookItemAdded`, `BookItemRemoved`

### 3. Lending

- Handles **borrowing, returning, and reservations**.
- Core transactional context.
- Emits: `LoanCreated`, `LoanReturned`, `ReservationPlaced`, `LoanOverdue`
- Interacts with `BookItem` to update availability.

---

## 🔄 Interactions Between Contexts

- When a **book is registered** in Catalog, Inventory can add physical copies.
- When a **loan is created** in Lending, Inventory is updated to mark the item unavailable.
- When a **book is returned**, Lending notifies Inventory to make it available again.
- Events like `LoanOverdue` or `ReservationPlaced` can trigger downstream notifications.

---

## 🌐 REST API Summary

| Method | Endpoint                               | Purpose                    | Context   |
|--------|----------------------------------------|----------------------------|-----------|
| GET    | `/catalog/books`                       | Search for books           | Catalog   |
| GET    | `/catalog/books/{isbn}`                | Get book details           | Catalog   |
| POST   | `/catalog/books`                       | Register a new book        | Catalog   |
| GET    | `/inventory/books/{isbn}/availability` | Check stock availability   | Inventory |
| POST   | `/inventory/books`                     | Add a physical copy        | Inventory |
| PATCH  | `/inventory/books/{bookItemId}`        | Update status (e.g., lost) | Inventory |
| POST   | `/lending/loans`                       | Borrow a book              | Lending   |
| POST   | `/lending/returns`                     | Return a book              | Lending   |
| GET    | `/lending/patrons/{patronId}/loans`    | View active loans          | Lending   |
| POST   | `/lending/reservations`                | Reserve a book             | Lending   |

---

## 🏗️ Architectural Intent

- **Loose coupling via domain events** enables future extraction into independently deployable microservices.
- **REST APIs** expose operations aligned with aggregates and use cases.
- **Clear bounded contexts** prevent domain leakage and support modular growth.

---