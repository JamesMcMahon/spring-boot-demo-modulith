package sh.jfm.springbootdemos.modulith.catalog;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    ResponseEntity<Book> create(@RequestBody BookRequest bookToCreate) {
        var inserted = catalog.add(bookToCreate.toBook());
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
    ResponseEntity<Void> update(
            @PathVariable String isbn,
            @RequestBody BookRequest bookToUpdate
    ) {
        if (!isbn.equals(bookToUpdate.isbn())) {
            return ResponseEntity.badRequest().build();
        }
        catalog.update(bookToUpdate.toBook());
        return ResponseEntity.noContent().build();
    }
}
