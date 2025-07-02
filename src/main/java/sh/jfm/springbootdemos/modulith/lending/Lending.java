package sh.jfm.springbootdemos.modulith.lending;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sh.jfm.springbootdemos.modulith.lendingevents.ReturnCopyEvent;

import java.time.LocalDate;
import java.util.List;

/// Business rules for lending copies of books
///
/// Marked `@Transactional` so every public method runs inside
/// a single JDBC transaction.
@Service
@Transactional
public class Lending {

    private final InventoryClient inventory;
    private final PatronRepository patronsRepo;
    private final LoanRepository loansRepo;
    private final ApplicationEventPublisher applicationEventPublisher;

    Lending(
            InventoryClient inventory,
            PatronRepository patronsRepo,
            LoanRepository loansRepo,
            ApplicationEventPublisher applicationEventPublisher
    ) {
        this.inventory = inventory;
        this.patronsRepo = patronsRepo;
        this.loansRepo = loansRepo;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public Loan borrow(long patronId, String isbn) {
        if (!patronsRepo.existsById(patronId)) {
            throw new PatronNotFoundException(patronId);
        }

        var lentCopy = inventory.markNextCopyAsUnavailable(isbn);
        return loansRepo.save(new Loan(
                lentCopy.id(),
                isbn,
                patronId,
                LocalDate.now().plusDays(30)
        ));
    }

    public void returnBook(long patronId, String isbn) {
        var loan = loansRepo.findByPatronIdAndIsbn(patronId, isbn)
                .orElseThrow(() -> new LoanNotFoundException(patronId, isbn));

        loansRepo.deleteById(loan.id());
        applicationEventPublisher.publishEvent(
                new ReturnCopyEvent(this, loan.copyId())
        );
    }

    public Patron addPatron(Patron patron) {
        if (patron.id() != null) {
            throw new IllegalArgumentException("Patron ID must be null when creating a new patron");
        }
        return patronsRepo.save(patron);
    }

    @Transactional(readOnly = true)
    public List<Loan> findLoansForPatron(long patronId) {
        if (!patronsRepo.existsById(patronId)) {
            throw new PatronNotFoundException(patronId);
        }
        return loansRepo.findByPatronId(patronId);
    }
}
