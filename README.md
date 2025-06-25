# Library Management System

A modular Spring Boot application for managing a library's book catalog, inventory, and lending operations.

## Project Overview

This application is designed as a modular monolith using Spring Boot and follows domain-driven design principles. 
It provides functionality for managing books, tracking inventory, and handling lending transactions.

## 🗺️ Domain

```mermaid
graph TD
%% external actor
    User("User")
%% Catalog domain
    subgraph CatalogDomain["Catalog"]
        Books["Books"]
    end

%% Inventory domain
    subgraph InventoryDomain["Inventory"]
        Copies["Copies"]
    end

%% Lending domain
    subgraph LendingDomain["Lending"]
        Loans["Loans"]
        Patrons["Patrons"]
    end

%% user → domain interactions (domain verbs)
    User -- " add / update / view " --> CatalogDomain
    User -- " add / remove Copies\nset availability\ncheck availability " --> InventoryDomain
    User -- " borrow / return Books\nview active Loans " --> LendingDomain
%% domain → domain interactions
    InventoryDomain -- " validate Book exists " --> CatalogDomain
    LendingDomain -- " lend / return Copies " --> InventoryDomain
```

## 📚 Catalog API – sample curl commands

### 1. Add a new book

```bash
curl -i -X POST http://localhost:8080/catalog/books \
     -H 'Content-Type: application/json' \
     -d '{"isbn":"9780132350884","title":"Clean Code","author":"Robert C. Martin"}'
```

• HTTP 201 Created, *Location* header set to `/catalog/books/9780132350884`.

### 2. Retrieve a book

```bash
curl -i http://localhost:8080/catalog/books/9780132350884
```

• HTTP 200 with the JSON payload.

### 3. Update a book

```bash
curl -i -X PATCH http://localhost:8080/catalog/books/9780132350884 \
     -H 'Content-Type: application/json' \
     -d '{"isbn":"9780132350884","title":"Cleaner Code","author":"Bob Martin"}'
```

• HTTP 204 No Content on success.

The commands assume the app is running locally on port 8080; adjust as needed.

## 📦 Inventory API – sample curl commands

### 1. Add a copy of a book

```bash
curl -i -X POST http://localhost:8080/inventory/copies \
     -H 'Content-Type: application/json' \
     -d '{"isbn":"9781416928171","location":"Main Library"}'
```

• HTTP 201 Created, *Location* header contains `/inventory/copies/{copyId}` with the generated numeric id.

### 2. Check availability

```bash
curl -i http://localhost:8080/inventory/books/9781416928171/availability
```

• HTTP 200 with a payload like `{"available":1}`.

### 3. Remove a copy

```bash
curl -i -X DELETE http://localhost:8080/inventory/copies/{copyId}
```

• HTTP 204 No Content on success.

> Replace `{copyId}` with the id returned in step 1.

## 🔄 Lending API – sample curl commands

### 1. Borrow a book

```bash
curl -i -X POST http://localhost:8080/lending/loans \
     -H 'Content-Type: application/json' \
     -d '{"patronId":1,"isbn":"9781416928171"}'
```

• HTTP 201 Created, *Location* header set to `/lending/loans/{loanId}`.

### 2. Return a book

```bash
curl -i -X POST http://localhost:8080/lending/returns \
     -H 'Content-Type: application/json' \
     -d '{"patronId":1,"isbn":"9781416928171"}'
```

• HTTP 204 No Content on success.

### 3. List active loans for a patron

```bash
curl -i http://localhost:8080/lending/patrons/1/loans
```

• HTTP 200 with a JSON array of the patron’s open loans.

