package sh.jfm.springbootdemos.modulith;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/// REST adapter for [Catalog].
/// The only responsibility is HTTP ↔︎ domain mapping.
@RestController
@RequestMapping("/catalog/books")
class BookController {

    private final Catalog catalog;

    BookController(Catalog catalog) {
        this.catalog = catalog;
    }

    @PostMapping
    ResponseEntity<Void> create(@RequestBody Book bookToCreate) {
        var inserted = catalog.add(bookToCreate);
        return ResponseEntity.created(URI.create("/catalog/books/" + inserted.isbn())).build();
    }

    @GetMapping("/{isbn}")
    ResponseEntity<Book> byIsbn(@PathVariable String isbn) {
        return catalog.byIsbn(isbn)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{isbn}")
    ResponseEntity<Void> update(@PathVariable String isbn, @RequestBody Book book) {
        if (!isbn.equals(book.isbn())) {                 // payload / path mismatch
            return ResponseEntity.badRequest().build();
        }
        catalog.update(book);
        return ResponseEntity.noContent().build();
    }
}
