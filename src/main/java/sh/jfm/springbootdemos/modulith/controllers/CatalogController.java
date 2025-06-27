package sh.jfm.springbootdemos.modulith.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sh.jfm.springbootdemos.modulith.model.Book;
import sh.jfm.springbootdemos.modulith.services.Catalog;

import java.net.URI;

/// REST adapter for [Catalog].
/// The only responsibility is HTTP ↔︎ domain mapping.
@RestController
@RequestMapping("/catalog/books")
class CatalogController {

    private final Catalog catalog;

    CatalogController(Catalog catalog) {
        this.catalog = catalog;
    }

    private record BookRequest(String isbn, String title, String author) {
        Book toBook() {
            return new Book(isbn, title, author);
        }
    }

    @Operation(
            summary = "Create a new book",
            tags = {"01 Catalog – Add Book"}
    )
    @PostMapping
    ResponseEntity<Book> create(@RequestBody BookRequest bookToCreate) {
        var inserted = catalog.add(bookToCreate.toBook());
        return ResponseEntity
                .created(URI.create("/catalog/books/" + inserted.isbn()))
                .body(inserted);
    }

    @Operation(
            summary = "Get book details by ISBN",
            tags = {"02 Catalog – Retrieve Book"}
    )
    @GetMapping("/{isbn}")
    ResponseEntity<Book> byIsbn(@Parameter(description = "ISBN of the book") @PathVariable String isbn) {
        return catalog.byIsbn(isbn)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Update an existing book",
            tags = {"03 Catalog – Update Book"}
    )
    @PatchMapping("/{isbn}")
    ResponseEntity<Void> update(
            @Parameter(description = "ISBN of the book") @PathVariable String isbn,
            @RequestBody BookRequest bookToUpdate
    ) {
        if (!isbn.equals(bookToUpdate.isbn())) {
            return ResponseEntity.badRequest().build();
        }
        catalog.update(bookToUpdate.toBook());
        return ResponseEntity.noContent().build();
    }
}
