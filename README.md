# Library Management System

A modular Spring Boot application for managing a library's book catalog, inventory, and lending operations.

## Project Overview

This application is designed as a modular monolith using Spring Boot and follows domain-driven design principles. 
It provides functionality for managing books, tracking inventory, and handling lending transactions.

## Architecture Overview

The project evolves from a monolithic design to a modular design using Spring Modulith. It is structured in stages:
1. **Functional Groups:** Initial organization by function (controllers, models, data access).
2. **Feature Groups:** Transition to bounded contexts (catalog, inventory, lending).
3. **Modular Design:** Final modular structure using Spring Modulith.

## Build Instructions

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Building the Application

1. Clone the repository
2. Navigate to the project directory
3. Run `./mvnw clean install` to build the project

## Running the Application

1. Execute `./mvnw spring-boot:run` to start the application
2. Access the application at `http://localhost:8080`

## REST API Endpoints

- **Catalog:**
  - `GET /catalog/books` - Retrieve all books
  - `POST /catalog/books` - Add a new book

- **Inventory:**
  - `GET /inventory/books/{isbn}/availability` - Check book availability

- **Lending:**
  - `POST /lending/loans` - Borrow a book
  - `POST /lending/returns` - Return a book

## Documentation

Refer to the `spec.md` for a detailed developer specification, including architecture, domain design, and testing plan.
