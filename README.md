# Library Management System

A modular Spring Boot application for managing a library's book catalog, inventory, and lending operations.

## Project Overview

This application is designed as a modular monolith using Spring Boot and follows domain-driven design principles. 
It provides functionality for managing books, tracking inventory, and handling lending transactions.

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

