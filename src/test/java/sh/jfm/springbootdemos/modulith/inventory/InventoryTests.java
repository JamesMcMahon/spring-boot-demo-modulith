package sh.jfm.springbootdemos.modulith.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import sh.jfm.springbootdemos.modulith.inventoryapi.NoAvailableCopiesException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/// Tests [Inventory] with a real H2 DB (`@DataJdbcTest`)
@DataJdbcTest
class InventoryTests {

    @Autowired
    private CopyRepository copyRepo;
    @Autowired
    private AvailableIsbnRepository isbnsRepo;

    private Inventory inventory;

    @BeforeEach
    void setup() {
        inventory = new Inventory(copyRepo, isbnsRepo);
    }

    @Test
    void addInsertsCopy() {
        inventory.registerIsbn("9780671698096");

        var inserted = inventory.add(new Copy("9780671698096", "Main Library"));

        assertThat(inserted.id()).isNotNull();
        assertThat(copyRepo.count()).isOne();
        assertThat(copyRepo.findById(inserted.id()))
                .contains(inserted);
    }

    @Test
    void addThrowsWhenIdIsPresent() {
        inventory.registerIsbn("9781416928171");

        assertThatThrownBy(
                () -> inventory.
                        add(new Copy(1L, "9781416928171", "Main Library", true))
        )
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(copyRepo.count()).isZero();
    }

    @Test
    void addThrowsWhenIsbnUnknown() {
        assertThatThrownBy(
                () -> inventory.
                        add(new Copy("unknown-isbn", "Main Library"))
        )
                .isInstanceOf(InvalidCopyException.class);

        assertThat(copyRepo.count()).isZero();
    }

    @Test
    void markNextCopyAsUnavailableUpdatesAvailability() {
        // arrange
        var isbn = "9780000009999";
        inventory.registerIsbn(isbn);
        var copy = inventory.add(new Copy(isbn, "Main Library"));

        assertThat(inventory.availability(isbn)).isEqualTo(1);

        // act
        var unavailableCopy = inventory.markNextCopyAsUnavailable(isbn);

        // assert – same copy returned, now unavailable and no copies free
        assertThat(unavailableCopy.id()).isEqualTo(copy.id());
        assertThat(unavailableCopy.available()).isEqualTo(false);
        assertThat(inventory.availability(isbn)).isZero();
    }

    @Test
    void markNextCopyAsUnavailableThrowsWhenNoAvailableCopies() {
        assertThatThrownBy(() -> inventory.markNextCopyAsUnavailable("already-unavailable-isbn"))
                .isInstanceOf(NoAvailableCopiesException.class);
    }

    @Test
    void markAsAvailableMarksCopyAsAvailableAndIncrementsCount() {
        // arrange – persist book and one copy, then mark it unavailable
        var isbn = "9780000007777";
        inventory.registerIsbn(isbn);
        var copy = inventory.add(new Copy(isbn, "Main Library"));
        inventory.markNextCopyAsUnavailable(isbn);
        assertThat(inventory.availability(isbn)).isZero();

        // act – return the copy
        inventory.markAsAvailable(copy.id());

        // assert – copy is now available and counted
        assertThat(copyRepo.findById(copy.id()))
                .map(Copy::available)
                .contains(true);
        assertThat(inventory.availability(isbn)).isEqualTo(1);
    }

    @Test
    void registerIsbnPersistsValue() {
        inventory.registerIsbn("111-1-11-111111-1");
        assertThat(isbnsRepo.existsByIsbn("111-1-11-111111-1")).isTrue();
    }

    @Test
    void registerIsbnIsIdempotent() {
        var isbn = "222-2-22-222222-2";
        inventory.registerIsbn(isbn);
        inventory.registerIsbn(isbn);

        assertThat(isbnsRepo.existsByIsbn(isbn)).isTrue();
        // still only one row
        assertThat(isbnsRepo.count()).isEqualTo(1);
    }
}
