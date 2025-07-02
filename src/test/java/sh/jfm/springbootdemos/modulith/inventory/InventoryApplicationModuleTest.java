package sh.jfm.springbootdemos.modulith.inventory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import sh.jfm.springbootdemos.modulith.catalogevents.BookAddedEvent;
import sh.jfm.springbootdemos.modulith.lendingevents.ReturnCopyEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.ANY;
import static org.springframework.modulith.test.ApplicationModuleTest.BootstrapMode.DIRECT_DEPENDENCIES;

/// Tests the interactions between the Inventory module and other modules.
@ApplicationModuleTest(mode = DIRECT_DEPENDENCIES)
// need to replace the database otherwise we have issues reapplying the schema
@AutoConfigureTestDatabase(replace = ANY)
public class InventoryApplicationModuleTest {

    @Autowired
    private Inventory inventory;
    @Autowired
    private AvailableIsbnRepository isbnsRepo;

    @Test
    void whenReceivingBookAddedEventRegistersIsbn(Scenario scenario) {
        var isbn = "9781416928171";

        // publish catalog event and wait for listener to complete
        scenario.publish(new BookAddedEvent(this, isbn))
                .andWaitForStateChange(() -> isbnsRepo.existsByIsbn(isbn))
                .andVerify(isbnExists -> assertThat(isbnExists).isTrue());
    }

    /// Tests the handling of a ReturnCopyEvent in the Inventory system.
    ///
    /// @param scenario Spring Modulith test scenario for event publishing and state verification
    @Test
    void whenReceivingAReturnEventMarkCopyAsAvailable(Scenario scenario) {
        var isbn = "9780671698096";
        inventory.registerIsbn(isbn);
        var copy = inventory.add(new Copy(isbn, "Main Library"));
        inventory.markNextCopyAsUnavailable(isbn);
        assertThat(inventory.availability(isbn)).isZero();

        scenario.publish(new ReturnCopyEvent(this, copy.id()))
                .andWaitForStateChange(() -> inventory.availability(isbn))
                .andVerify(numberOfAvailable -> assertThat(numberOfAvailable)
                        .isOne());
    }
}
