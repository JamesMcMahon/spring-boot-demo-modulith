package sh.jfm.springbootdemos.modulith.services;

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

    public Inventory(CopyRepository copies,
                     BookRepository books) {
        this.copies = copies;
        this.books = books;
    }

    public Copy add(Copy copy) {
        if (copy.id() != null) {
            throw new IllegalArgumentException("Copy ID must be null when creating a new copy");
        }
        if (!books.existsByIsbn(copy.isbn())) {
            throw new BookNotFoundException(copy.isbn());
        }
        return copies.save(copy);
    }

    public void remove(long id) {
        if (!copies.existsById(id)) {
            throw new CopyNotFoundException(id);
        }
        copies.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long availability(String isbn) {
        return copies.countByIsbnAndAvailableTrue(isbn);
    }

    public void setAvailability(long id, boolean available) {
        var existing = copies.findById(id)
                .orElseThrow(() -> new CopyNotFoundException(id));
        copies.save(new Copy(
                id,
                existing.isbn(),
                existing.location(),
                available
        ));
    }
}
