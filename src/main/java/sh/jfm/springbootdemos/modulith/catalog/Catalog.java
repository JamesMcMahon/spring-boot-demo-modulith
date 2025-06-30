package sh.jfm.springbootdemos.modulith.catalog;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/// Encapsulates all business rules for adding / updating [Book].
/// Controllers delegate here to keep HTTP layer free of logic
/// (DDD layered architecture).
///
/// Marked `@Transactional` so every public method runs inside
/// a single JDBC transaction.
@Service
@Transactional
public class Catalog {

    private final BookRepository bookRepo;

    Catalog(BookRepository bookRepo) {
        this.bookRepo = bookRepo;
    }

    public Book add(Book book) {
        if (book.id() != null) {
            throw new IllegalArgumentException("Book ID must be null when creating a new book");
        }
        if (bookRepo.existsByIsbn(book.isbn())) {
            throw new BookAlreadyExistsException(book.isbn());
        }
        return bookRepo.save(book);
    }

    public void update(Book book) {
        var existing = bookRepo.findByIsbn(book.isbn())
                .orElseThrow(() -> new BookNotFoundException(book.isbn()));
        bookRepo.save(new Book(existing.id(), book.isbn(), book.title(), book.author()));
    }

    @Transactional(readOnly = true)
    public Optional<Book> byIsbn(String isbn) {
        return bookRepo.findByIsbn(isbn);
    }

    public boolean existsByIsbn(String isbn) {
        return bookRepo.existsByIsbn(isbn);
    }
}
