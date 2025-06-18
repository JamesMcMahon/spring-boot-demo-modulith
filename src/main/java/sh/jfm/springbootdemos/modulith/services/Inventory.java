package sh.jfm.springbootdemos.modulith.services;

import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sh.jfm.springbootdemos.modulith.data.BookRepository;
import sh.jfm.springbootdemos.modulith.data.CopyRepository;
import sh.jfm.springbootdemos.modulith.model.Copy;

/// Business rules for managing physical copies
///
/// Marked `@Transactional` so every public method runs inside
/// a single JDBC transaction.
@Service
@Transactional
public class Inventory {

    private final CopyRepository copies;
    private final BookRepository books;
    private final JdbcAggregateTemplate template;

    public Inventory(CopyRepository copies,
                     BookRepository books,
                     JdbcAggregateTemplate template) {
        this.copies = copies;
        this.books = books;
        this.template = template;
    }

    public Copy add(Copy copy) {
        if (!books.existsByIsbn(copy.isbn())) {
            throw new BookNotFoundException(copy.isbn());
        }
        return template.insert(copy);
    }

    public void remove(long id) {
        if (!copies.existsById(id)) {
            throw new CopyNotFoundException(id);
        }
        copies.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long availability(String isbn) {
        return copies.countByIsbn(isbn);
    }
}
