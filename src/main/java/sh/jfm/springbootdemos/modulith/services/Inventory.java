package sh.jfm.springbootdemos.modulith.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sh.jfm.springbootdemos.modulith.data.CopyRepository;
import sh.jfm.springbootdemos.modulith.model.Copy;

/// Business rules for managing physical copies
///
/// Marked `@Transactional` so every public method runs inside
/// a single JDBC transaction.
@Service
@Transactional
public class Inventory {

    private final CopyRepository copiesRepo;
    private final Catalog catalog;

    public Inventory(CopyRepository copies, Catalog catalog) {
        this.copiesRepo = copies;
        this.catalog = catalog;
    }

    public Copy add(Copy copy) {
        if (copy.id() != null) {
            throw new IllegalArgumentException("Copy ID must be null when creating a new copy");
        }
        if (!catalog.existsByIsbn(copy.isbn())) {
            throw new BookNotFoundException(copy.isbn());
        }
        return copiesRepo.save(copy);
    }

    public void remove(long id) {
        if (!copiesRepo.existsById(id)) {
            throw new CopyNotFoundException(id);
        }
        copiesRepo.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long availability(String isbn) {
        return copiesRepo.countByIsbnAndAvailableTrue(isbn);
    }

    public void setAvailability(long id, boolean available) {
        var existing = copiesRepo.findById(id)
                .orElseThrow(() -> new CopyNotFoundException(id));
        copiesRepo.save(new Copy(
                id,
                existing.isbn(),
                existing.location(),
                available
        ));
    }

    public void markAsAvailable(long copyId) {
        setAvailability(copyId, true);
    }

    public Copy markAsUnavailable(String isbn) {
        var copy = copiesRepo.findFirstByIsbnAndAvailableTrue(isbn)
                .orElseThrow(() -> new NoAvailableCopiesException(isbn));
        setAvailability(copy.id(), false);
        return copy;
    }
}
