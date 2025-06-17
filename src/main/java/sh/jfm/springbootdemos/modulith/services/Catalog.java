package sh.jfm.springbootdemos.modulith.services;

import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
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
    private final JdbcAggregateTemplate template;

    public Catalog(BookRepository repo, JdbcAggregateTemplate template) {
        this.repo = repo;
        this.template = template;
    }

    public Book add(Book book) {
        if (repo.existsById(book.isbn())) {
            throw new BookAlreadyExistsException(book.isbn());
        }
        return template.insert(book);
    }

    public void update(Book book) {
        if (!repo.existsById(book.isbn())) {
            throw new BookNotFoundException(book.isbn());
        }
        repo.save(book);
    }

    @Transactional(readOnly = true)
    public Optional<Book> byIsbn(String isbn) {
        return repo.findById(isbn);
    }
}
