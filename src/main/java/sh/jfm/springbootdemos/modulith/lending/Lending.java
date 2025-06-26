package sh.jfm.springbootdemos.modulith.lending;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sh.jfm.springbootdemos.modulith.inventory.Inventory;

import java.time.LocalDate;
import java.util.List;

/// Business rules for lending copies of books
///
/// Marked `@Transactional` so every public method runs inside
/// a single JDBC transaction.
@Service
@Transactional
public class Lending {

    private final Inventory inventory;
    private final PatronRepository patronsRepo;
    private final LoanRepository loansRepo;

    public Lending(Inventory inventory,
                   PatronRepository patronsRepo,
                   LoanRepository loansRepo) {
        this.inventory = inventory;
        this.patronsRepo = patronsRepo;
        this.loansRepo = loansRepo;
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
        inventory.markAsAvailable(loan.copyId());
    }

    @Transactional(readOnly = true)
    public List<Loan> findLoansForPatron(long patronId) {
        if (!patronsRepo.existsById(patronId)) {
            throw new PatronNotFoundException(patronId);
        }
        return loansRepo.findByPatronId(patronId);
    }
}
