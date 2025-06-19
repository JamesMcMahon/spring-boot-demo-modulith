package sh.jfm.springbootdemos.modulith.service;

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
import sh.jfm.springbootdemos.modulith.services.Inventory;
import sh.jfm.springbootdemos.modulith.services.Lending;
import sh.jfm.springbootdemos.modulith.services.NoAvailableCopiesException;
import sh.jfm.springbootdemos.modulith.services.PatronNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJdbcTest
class LendingTests {

    @Autowired
    private BookRepository books;
    @Autowired
    private CopyRepository copies;
    @Autowired
    private PatronRepository patrons;
    @Autowired
    private LoanRepository loans;

    private Inventory inventory;
    private Lending lending;
    private long patronId;

    @BeforeEach
    void setUp() {
        inventory = new Inventory(copies, books);
        lending = new Lending(inventory, patrons, loans);
        patronId = patrons.save(new Patron(null)).id();
    }

    @Test
    void borrowCreatesLoanAndMarksCopyUnavailable() {
        var loan = borrowBook("123");

        assertThat(loans.findById(loan.id())).isPresent();
        assertThat(copies.findById(loan.copyId()))
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

        assertThat(loans.existsById(loan.id())).isFalse();
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

    @SuppressWarnings("SameParameterValue")
    private Loan borrowBook(String isbn) {
        books.save(new Book(null, isbn, "Title", "Author"));
        copies.save(new Copy(null, isbn, "A-1", true));
        return lending.borrow(patronId, isbn);
    }
}
