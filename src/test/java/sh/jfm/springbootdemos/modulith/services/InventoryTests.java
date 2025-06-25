package sh.jfm.springbootdemos.modulith.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import sh.jfm.springbootdemos.modulith.data.BookRepository;
import sh.jfm.springbootdemos.modulith.data.CopyRepository;
import sh.jfm.springbootdemos.modulith.model.Book;
import sh.jfm.springbootdemos.modulith.model.Copy;

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
        inventory = new Inventory(copyRepo, bookRepo);
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
    void removeDeletesCopy() {
        bookRepo.save(new Book("9780671698097", "The Celery Stalks at Midnight", "Deborah and James Howe"));
        var inserted = inventory.add(new Copy("9780671698097", "Main Library"));

        inventory.remove(inserted.id());

        assertThat(copyRepo.count()).isZero();
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
                .isInstanceOf(BookNotFoundException.class);

        assertThat(copyRepo.count()).isZero();
    }

    @Test
    void removeThrowsWhenIdMissing() {
        bookRepo.save(new Book("9780689315484", "Nighty-Nightmare", "Deborah and James Howe"));

        assertThatThrownBy(() -> inventory.remove(12345L))
                .isInstanceOf(CopyNotFoundException.class);

        assertThat(copyRepo.count()).isZero();
    }

    @Test
    void setAvailabilityTogglesFlagAndAffectsCount() {
        // arrange – persist a referenced book and one available copy
        bookRepo.save(new Book("9780000000001", "Some Book", "Some Author"));
        var copy = inventory.add(new Copy("9780000000001", "Main Library"));

        assertThat(inventory.availability("9780000000001")).isEqualTo(1);

        // act – mark copy unavailable
        inventory.setAvailability(copy.id(), false);

        // assert – count dropped
        assertThat(inventory.availability("9780000000001")).isEqualTo(0);

        // act – mark available again
        inventory.setAvailability(copy.id(), true);

        // assert – count restored
        assertThat(inventory.availability("9780000000001")).isEqualTo(1);
    }

    @Test
    void lendAvailableCopyMarksCopyUnavailableAndReturnsIt() {
        // arrange
        var isbn = "9780000009999";
        bookRepo.save(new Book(isbn, "Borrowable Book", "Some Author"));
        var copy = inventory.add(new Copy(isbn, "Main Library"));

        assertThat(inventory.availability(isbn)).isEqualTo(1);

        // act
        var lent = inventory.lendAvailableCopy(isbn);

        // assert – same copy returned, now unavailable and no copies free
        assertThat(lent.id()).isEqualTo(copy.id());
        assertThat(copyRepo.findById(copy.id()))
                .map(Copy::available)
                .contains(false);
        assertThat(inventory.availability(isbn)).isZero();
    }

    @Test
    void lendAvailableCopyThrowsWhenNoAvailableCopies() {
        var isbn = "9780000008888";
        bookRepo.save(new Book(isbn, "Unborrowable Book", "Some Author"));

        assertThatThrownBy(() -> inventory.lendAvailableCopy(isbn))
                .isInstanceOf(NoAvailableCopiesException.class);
    }

    @Test
    void returnCopyMarksCopyAvailableAndIncrementsCount() {
        // arrange – persist book and one copy, then mark it unavailable
        var isbn = "9780000007777";
        bookRepo.save(new Book(isbn, "Returned-Book", "Some Author"));
        var copy = inventory.add(new Copy(isbn, "Main Library"));
        inventory.setAvailability(copy.id(), false);
        assertThat(inventory.availability(isbn)).isZero();

        // act – return the copy
        inventory.returnCopy(copy.id());

        // assert – copy is now available and counted
        assertThat(copyRepo.findById(copy.id()))
                .map(Copy::available)
                .contains(true);
        assertThat(inventory.availability(isbn)).isEqualTo(1);
    }
}
