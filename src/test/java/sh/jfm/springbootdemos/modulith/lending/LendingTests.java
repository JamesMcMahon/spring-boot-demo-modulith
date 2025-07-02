package sh.jfm.springbootdemos.modulith.lending;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import sh.jfm.springbootdemos.modulith.inventory.Copy;
import sh.jfm.springbootdemos.modulith.inventory.Inventory;
import sh.jfm.springbootdemos.modulith.inventory.NoAvailableCopiesException;
import sh.jfm.springbootdemos.modulith.lendingevents.ReturnCopyEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@DataJdbcTest
@Import(Inventory.class)
class LendingTests {

    @Autowired
    private PatronRepository patronRepo;
    @Autowired
    private LoanRepository loanRepo;
    @Autowired
    private Inventory inventory;
    @MockitoBean
    private ApplicationEventPublisher testApplicationEventPublisher;

    private Lending lending;
    private long patronId;

    @BeforeEach
    void setUp() {
        lending = new Lending(inventory, patronRepo, loanRepo, testApplicationEventPublisher);
        patronId = lending.addPatron(new Patron("Test", "User")).id();
    }

    @Test
    void borrowCreatesLoanAndMarksCopyUnavailable() {
        var loan = borrowBook("123");

        assertThat(loanRepo.findById(loan.id())).isPresent();
        assertThat(inventory.availability("123")).isZero();
    }

    @Test
    void borrowThrowsWhenNoCopiesFree() {
        borrowBook("123");

        assertThatThrownBy(() -> lending.borrow(patronId, "123"))
                .isInstanceOf(NoAvailableCopiesException.class);
    }

    @Test
    void returnDeletesLoanAndSendsAReturnsCopyEvent() {
        var loan = borrowBook("123");

        lending.returnBook(patronId, "123");

        assertThat(loanRepo.existsById(loan.id())).isFalse();
        verify(testApplicationEventPublisher).publishEvent(argThat(event ->
                event instanceof ReturnCopyEvent &&
                ((ReturnCopyEvent) event).getCopyId().equals(loan.copyId())
        ));
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
        inventory.registerIsbn(isbn);
        inventory.add(new Copy(isbn, "A-1"));
        return lending.borrow(patronId, isbn);
    }
}
