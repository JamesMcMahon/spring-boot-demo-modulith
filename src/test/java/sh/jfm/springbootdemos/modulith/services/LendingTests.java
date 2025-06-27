package sh.jfm.springbootdemos.modulith.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import sh.jfm.springbootdemos.modulith.data.BookRepository;
import sh.jfm.springbootdemos.modulith.data.CopyRepository;
import sh.jfm.springbootdemos.modulith.data.LoanRepository;
import sh.jfm.springbootdemos.modulith.data.PatronRepository;
import sh.jfm.springbootdemos.modulith.model.Book;
import sh.jfm.springbootdemos.modulith.model.Copy;
import sh.jfm.springbootdemos.modulith.model.Loan;
import sh.jfm.springbootdemos.modulith.model.Patron;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJdbcTest
class LendingTests {

    @Autowired
    private BookRepository bookRepo;
    @Autowired
    private CopyRepository copyRepo;
    @Autowired
    private PatronRepository patronRepo;
    @Autowired
    private LoanRepository loanRepo;

    private Inventory inventory;
    private Lending lending;
    private long patronId;

    @BeforeEach
    void setUp() {
        inventory = new Inventory(copyRepo, new Catalog(bookRepo));
        lending = new Lending(inventory, patronRepo, loanRepo);
        patronId = lending.addPatron(new Patron("Test", "User")).id();
    }

    @Test
    void borrowCreatesLoanAndMarksCopyUnavailable() {
        var loan = borrowBook("123");

        assertThat(loanRepo.findById(loan.id())).isPresent();
        assertThat(copyRepo.findById(loan.copyId()))
                .get()
                .extracting(Copy::available)
                .isEqualTo(false);
        assertThat(inventory.availability("123")).isZero();
    }

    @Test
    void borrowThrowsWhenNoCopiesFree() {
        borrowBook("123");

        assertThatThrownBy(() -> lending.borrow(patronId, "123"))
                .isInstanceOf(NoAvailableCopiesException.class);
    }

    @Test
    void returnDeletesLoanAndReturnsCopy() {
        var loan = borrowBook("123");

        lending.returnBook(patronId, "123");

        assertThat(loanRepo.existsById(loan.id())).isFalse();
        assertThat(inventory.availability("123")).isEqualTo(1);
    }

    @Test
    void findLoansReturnsOnlyLoansForPatron() {
        var loanForPatron = borrowBook("123");

        assertThat(lending.findLoansForPatron(patronId))
                .containsExactly(loanForPatron);
    }

    @Test
    void findLoansReturnsEmptyAfterReturn() {
        borrowBook("123");
        lending.returnBook(patronId, "123");

        assertThat(lending.findLoansForPatron(patronId)).isEmpty();
    }

    @Test
    void findLoansForPatronThrowsWhenPatronMissing() {
        assertThatThrownBy(() -> lending.findLoansForPatron(42L))
                .isInstanceOf(PatronNotFoundException.class);
    }

    @Test
    void addPatronInsertsPatron() {
        var saved = lending.addPatron(new Patron("Test", "User"));
        assertThat(patronRepo.findById(saved.id())).isPresent();
    }

    @Test
    void addPatronFailsWhenIdPresent() {
        assertThatThrownBy(() -> lending.addPatron(new Patron(666L, "Test", "User")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @SuppressWarnings("SameParameterValue")
    private Loan borrowBook(String isbn) {
        bookRepo.save(new Book(isbn, "Title", "Author"));
        copyRepo.save(new Copy(isbn, "A-1"));
        return lending.borrow(patronId, isbn);
    }
}
