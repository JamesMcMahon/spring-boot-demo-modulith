package sh.jfm.springbootdemos.modulith.lending;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import sh.jfm.springbootdemos.modulith.inventory.Copy;
import sh.jfm.springbootdemos.modulith.inventory.Inventory;
import sh.jfm.springbootdemos.modulith.lendingevents.ReturnCopyEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.ANY;
import static org.springframework.modulith.test.ApplicationModuleTest.BootstrapMode.ALL_DEPENDENCIES;

/// Tests the interactions between the Lending module and other modules.
@ApplicationModuleTest(mode = ALL_DEPENDENCIES, extraIncludes = "inventory")
// need to replace the database otherwise we have issues reapplying the schema
@AutoConfigureTestDatabase(replace = ANY)
public class LendingApplicationModuleTest {

    @Autowired
    private Inventory inventory;
    @Autowired
    private Lending lending;

    /// Tests that returning a book triggers a ReturnCopyEvent.
    ///
    /// @param scenario Spring Modulith test scenario for event verification
    @Test
    void returningABookPublishesAnEvent(Scenario scenario) {
        var loan = borrowBook(
                lending.addPatron(new Patron("Test", "User")).id(),
                "123"
        );
        scenario.stimulate(() -> lending.returnBook(loan.patronId(), loan.isbn()))
                .andWaitForEventOfType(ReturnCopyEvent.class)
                .toArriveAndVerify(event -> assertThat(event)
                        .hasFieldOrPropertyWithValue("copyId", loan.copyId())
                        .hasFieldOrProperty("timestamp"));
    }

    @SuppressWarnings("SameParameterValue")
    private Loan borrowBook(long patronId, String isbn) {
        inventory.registerIsbn(isbn);
        inventory.add(new Copy(isbn, "A-1"));
        return lending.borrow(patronId, isbn);
    }
}
