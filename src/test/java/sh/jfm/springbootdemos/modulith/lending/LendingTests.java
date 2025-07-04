package sh.jfm.springbootdemos.modulith.lending;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import sh.jfm.springbootdemos.modulith.inventoryapi.InventoryApi;
import sh.jfm.springbootdemos.modulith.lendingevents.ReturnCopyEvent;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DataJdbcTest
class LendingTests {

    @Autowired
    private PatronRepository patronRepo;
    @Autowired
    private LoanRepository loanRepo;
    @MockitoBean
    private InventoryApi inventoryApi;
    @MockitoBean
    private ApplicationEventPublisher testApplicationEventPublisher;

    private Lending lending;
    private long patronId;

    @BeforeEach
    void setUp() {
        lending = new Lending(
                inventoryApi,
                patronRepo,
                loanRepo,
                testApplicationEventPublisher
        );
        patronId = lending.addPatron(new Patron("Test", "User")).id();

        when(inventoryApi.markNextCopyAsUnavailable(any())).thenReturn(Optional.of(1L));
    }

    @Test
    void borrowCreatesLoanAndMarksCopyUnavailable() {
        var loan = lending.borrow(patronId, "123");

        assertThat(loanRepo.findById(loan.id())).isPresent();
        verify(inventoryApi).markNextCopyAsUnavailable("123");
    }

    @Test
    void borrowThrowsWhenInventoryReturnsEmpty() {
        when(inventoryApi.markNextCopyAsUnavailable("123"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> lending.borrow(patronId, "123"))
                .isInstanceOf(NoAvailableCopiesException.class);
    }

    @Test
    void returnDeletesLoanAndSendsAReturnsCopyEvent() {
        var loan = lending.borrow(patronId, "123");

        lending.returnBook(patronId, "123");

        assertThat(loanRepo.existsById(loan.id())).isFalse();
        verify(testApplicationEventPublisher).publishEvent(argThat(event ->
                event instanceof ReturnCopyEvent &&
                ((ReturnCopyEvent) event).getCopyId().equals(loan.copyId())
        ));
    }

    @Test
    void findLoansReturnsOnlyLoansForPatron() {
        var loanForPatron = lending.borrow(patronId, "123");

        assertThat(lending.findLoansForPatron(patronId))
                .containsExactly(loanForPatron);
    }

    @Test
    void findLoansReturnsEmptyAfterReturn() {
        lending.borrow(patronId, "123");
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
}
