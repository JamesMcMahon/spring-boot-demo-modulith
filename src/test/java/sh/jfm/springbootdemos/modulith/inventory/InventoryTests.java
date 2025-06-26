package sh.jfm.springbootdemos.modulith.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import sh.jfm.springbootdemos.modulith.catalog.Book;
import sh.jfm.springbootdemos.modulith.catalog.BookRepository;
import sh.jfm.springbootdemos.modulith.catalog.Catalog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/// Tests [Inventory] with a real H2 DB (`@DataJdbcTest`)
@DataJdbcTest
class InventoryTests {

    @Autowired
    private BookRepository bookRepo;
    @Autowired
    private CopyRepository copyRepo;

    private Inventory inventory;

    @BeforeEach
    void setup() {
        inventory = new Inventory(copyRepo, new Catalog(bookRepo));
    }

    @Test
    void addInsertsCopy() {
        bookRepo.save(new Book("9780671698096", "Howliday Inn", "Deborah and James Howe"));

        var inserted = inventory.add(new Copy("9780671698096", "Main Library"));

        assertThat(inserted.id()).isNotNull();
        assertThat(copyRepo.count()).isOne();
        assertThat(copyRepo.findById(inserted.id()))
                .contains(inserted);
    }

    @Test
    void addThrowsWhenIdIsPresent() {
        bookRepo.save(new Book("9781416928171", "Bunnicula Meets Edgar Allan Crow", "James Howe"));

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
        bookRepo.save(new Book(isbn, "Borrowable Book", "Some Author"));
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
        bookRepo.save(new Book(isbn, "Returned-Book", "Some Author"));
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
}
