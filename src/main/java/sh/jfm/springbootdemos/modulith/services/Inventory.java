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
            throw new InvalidCopyException(copy.isbn());
        }
        return copiesRepo.save(copy);
    }

    @Transactional(readOnly = true)
    public long availability(String isbn) {
        return copiesRepo.countByIsbnAndAvailableTrue(isbn);
    }

    public void markAsAvailable(long copyId) {
        var existing = copiesRepo.findById(copyId)
                .orElseThrow(() -> new CopyNotFoundException(copyId));
        updateCopyAvailability(existing, true);
    }

    public Copy markNextCopyAsUnavailable(String isbn) {
        var existing = copiesRepo.findFirstByIsbnAndAvailableTrue(isbn)
                .orElseThrow(() -> new NoAvailableCopiesException(isbn));
        return updateCopyAvailability(existing, false);
    }

    private Copy updateCopyAvailability(Copy existing, boolean available) {
        return copiesRepo.save(new Copy(
                existing.id(),
                existing.isbn(),
                existing.location(),
                available
        ));
    }
}
