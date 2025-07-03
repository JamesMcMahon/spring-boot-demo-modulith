package sh.jfm.springbootdemos.modulith.lending;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import sh.jfm.springbootdemos.modulith.inventoryapi.InventoryApi;
import sh.jfm.springbootdemos.modulith.lendingevents.ReturnCopyEvent;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.ANY;
import static org.springframework.modulith.test.ApplicationModuleTest.BootstrapMode.ALL_DEPENDENCIES;

/// Tests the interactions between the Lending module and other modules.
@ApplicationModuleTest(mode = ALL_DEPENDENCIES)
// need to replace the database otherwise we have issues reapplying the schema
@AutoConfigureTestDatabase(replace = ANY)
public class LendingApplicationModuleTest {

    @Autowired
    private Lending lending;
    @MockitoBean
    private InventoryApi mockInventoryApi;

    /// Tests that returning a book triggers a ReturnCopyEvent.
    ///
    /// @param scenario Spring Modulith test scenario for event verification
    @Test
    void returningABookPublishesAnEvent(Scenario scenario) {
        when(mockInventoryApi.markNextCopyAsUnavailable("123"))
                .thenReturn(Optional.of(777L));
        var patron = lending.addPatron(new Patron("Test", "User"));
        var loan = lending.borrow(patron.id(), "123");

        scenario.stimulate(() -> lending.returnBook(loan.patronId(), loan.isbn()))
                .andWaitForEventOfType(ReturnCopyEvent.class)
                .toArriveAndVerify(event -> assertThat(event)
                        .hasFieldOrPropertyWithValue("copyId", loan.copyId())
                        .hasFieldOrProperty("timestamp"));
    }

}
