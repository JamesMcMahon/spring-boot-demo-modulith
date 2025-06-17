package sh.jfm.springbootdemos.modulith.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sh.jfm.springbootdemos.modulith.data.BookRepository;
import sh.jfm.springbootdemos.modulith.model.Book;

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

    private final BookRepository repo;

    public Catalog(BookRepository repo) {
        this.repo = repo;
    }

    public Book add(Book book) {
        if (book.id() != null) {
            throw new IllegalArgumentException("Book ID must be null when creating a new book");
        }
        if (repo.existsByIsbn(book.isbn())) {
            throw new BookAlreadyExistsException(book.isbn());
        }
        return repo.save(book);
    }

    public void update(Book book) {
        var existing = repo.findByIsbn(book.isbn())
                .orElseThrow(() -> new BookNotFoundException(book.isbn()));
        repo.save(new Book(existing.id(), book.isbn(), book.title(), book.author()));
    }

    @Transactional(readOnly = true)
    public Optional<Book> byIsbn(String isbn) {
        return repo.findByIsbn(isbn);
    }
}
