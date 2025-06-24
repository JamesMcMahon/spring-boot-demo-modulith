package sh.jfm.springbootdemos.modulith.controllers;

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

    @PostMapping
    ResponseEntity<Book> create(@RequestBody Book bookToCreate) {
        var inserted = catalog.add(bookToCreate);
        return ResponseEntity
                .created(URI.create("/catalog/books/" + inserted.isbn()))
                .body(inserted);
    }

    @GetMapping("/{isbn}")
    ResponseEntity<Book> byIsbn(@PathVariable String isbn) {
        return catalog.byIsbn(isbn)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{isbn}")
    ResponseEntity<Void> update(@PathVariable String isbn, @RequestBody Book book) {
        if (!isbn.equals(book.isbn())) {
            return ResponseEntity.badRequest().build();
        }
        catalog.update(book);
        return ResponseEntity.noContent().build();
    }
}
