package sh.jfm.springbootdemos.modulith.inventory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/// Business rules for managing physical copies
///
/// Marked `@Transactional` so every public method runs inside
/// a single JDBC transaction.
@Service
@Transactional
public class Inventory {

    private final CopyRepository copiesRepo;
    private final AvailableIsbnRepository isbnsRepo;

    Inventory(CopyRepository copies, AvailableIsbnRepository isbns) {
        this.copiesRepo = copies;
        this.isbnsRepo = isbns;
    }

    public void registerIsbn(String isbn) {
        if (!isbnsRepo.existsByIsbn(isbn)) {
            isbnsRepo.save(new AvailableIsbn(isbn));
        }
    }

    public Copy add(Copy copy) {
        if (copy.id() != null) {
            throw new IllegalArgumentException("Copy ID must be null when creating a new copy");
        }
        if (!isbnsRepo.existsByIsbn(copy.isbn())) {
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

    public Optional<Copy> markNextCopyAsUnavailable(String isbn) {
        return copiesRepo
                .findFirstByIsbnAndAvailableTrue(isbn)
                .map(existing -> updateCopyAvailability(existing, false));
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
