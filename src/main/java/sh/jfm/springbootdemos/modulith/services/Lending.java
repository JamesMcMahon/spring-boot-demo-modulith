package sh.jfm.springbootdemos.modulith.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sh.jfm.springbootdemos.modulith.data.LoanRepository;
import sh.jfm.springbootdemos.modulith.data.PatronRepository;
import sh.jfm.springbootdemos.modulith.model.Loan;

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
    private final PatronRepository patrons;
    private final LoanRepository loans;

    public Lending(Inventory inventory,
                   PatronRepository patrons,
                   LoanRepository loans) {
        this.inventory = inventory;
        this.patrons = patrons;
        this.loans = loans;
    }

    public Loan borrow(long patronId, String isbn) {
        if (!patrons.existsById(patronId)) {
            throw new PatronNotFoundException(patronId);
        }

        var lentCopy = inventory.lendAvailableCopy(isbn);
        return loans.save(new Loan(
                lentCopy.id(),
                isbn,
                patronId,
                LocalDate.now().plusDays(30)
        ));
    }

    public void returnBook(long patronId, String isbn) {
        var loan = loans.findByPatronIdAndIsbn(patronId, isbn)
                .orElseThrow(() -> new LoanNotFoundException(patronId, isbn));

        loans.deleteById(loan.id());
        inventory.returnCopy(loan.copyId());
    }

    @Transactional(readOnly = true)
    public List<Loan> findLoansForPatron(long patronId) {
        if (!patrons.existsById(patronId)) {
            throw new PatronNotFoundException(patronId);
        }
        return loans.findByPatronId(patronId);
    }
}
