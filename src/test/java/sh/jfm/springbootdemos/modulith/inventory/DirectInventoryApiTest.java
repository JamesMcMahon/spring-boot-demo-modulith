package sh.jfm.springbootdemos.modulith.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
class DirectInventoryApiTest {

    @Autowired
    private CopyRepository copyRepo;
    @Autowired
    private AvailableIsbnRepository isbnsRepo;

    private Inventory inventory;
    private DirectInventoryApi api;

    @BeforeEach
    void setUp() {
        inventory = new Inventory(copyRepo, isbnsRepo);
        api = new DirectInventoryApi(inventory);
    }

    @Test
    void delegatesToInventoryAndReturnsCopyId() {
        String isbn = "9780123456789";
        inventory.registerIsbn(isbn);
        Copy copy = inventory.add(new Copy(isbn, "Shelf-1"));

        Optional<Long> returnedId = api.markNextCopyAsUnavailable(isbn);

        assertThat(returnedId).contains(copy.id());
    }

    @Test
    void returnsNullWhenNoCopiesAvailable() {
        assertThat(api.markNextCopyAsUnavailable("copy-does-not-exist"))
                .isEmpty();
    }
}
