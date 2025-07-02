package sh.jfm.springbootdemos.modulith.catalog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import sh.jfm.springbootdemos.modulith.catalogevents.BookAddedEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.ANY;

@ApplicationModuleTest
// need to replace the database otherwise we have issues reapplying the schema
@AutoConfigureTestDatabase(replace = ANY)
class CatalogApplicationModuleTest {

    @Autowired
    Catalog catalog;

    @Test
    void publishesBookAddedEvent(Scenario scenario) {
        scenario.stimulate(() -> catalog.add(new Book("9780123456789", "The Title", "An Author")))
                .andWaitForEventOfType(BookAddedEvent.class)
                .toArriveAndVerify(event -> assertThat(event.getIsbn()).isEqualTo("9780123456789"));
    }
}
