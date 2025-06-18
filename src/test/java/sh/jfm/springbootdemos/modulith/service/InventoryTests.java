package sh.jfm.springbootdemos.modulith.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import sh.jfm.springbootdemos.modulith.data.BookRepository;
import sh.jfm.springbootdemos.modulith.data.CopyRepository;
import sh.jfm.springbootdemos.modulith.model.Book;
import sh.jfm.springbootdemos.modulith.model.Copy;
import sh.jfm.springbootdemos.modulith.services.BookNotFoundException;
import sh.jfm.springbootdemos.modulith.services.CopyNotFoundException;
import sh.jfm.springbootdemos.modulith.services.Inventory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/// Tests [Inventory] with a real H2 DB (`@DataJdbcTest`)
@DataJdbcTest
class InventoryTests {

    @Autowired
    BookRepository bookRepo;
    @Autowired
    CopyRepository copyRepo;

    @Test
    void addInsertsCopy() {
        bookRepo.save(new Book("9780671698096", "Howliday Inn", "Deborah and James Howe"));

        var inserted = new Inventory(copyRepo, bookRepo)
                .add(new Copy("9780671698096", "Main Library"));

        assertThat(inserted.id()).isNotNull();
        assertThat(copyRepo.count()).isOne();
        assertThat(copyRepo.findById(inserted.id()))
                .contains(inserted);
    }

    @Test
    void removeDeletesCopy() {
        bookRepo.save(new Book("9780671698097", "The Celery Stalks at Midnight", "Deborah and James Howe"));
        var inventory = new Inventory(copyRepo, bookRepo);
        var inserted = inventory.add(new Copy("9780671698097", "Main Library"));

        inventory.remove(inserted.id());

        assertThat(copyRepo.count()).isZero();
    }

    @Test
    void addThrowsWhenIdIsPresent() {
        bookRepo.save(new Book("9781416928171", "Bunnicula Meets Edgar Allan Crow", "James Howe"));

        assertThatThrownBy(
                () -> new Inventory(copyRepo, bookRepo).
                        add(new Copy(1L, "9781416928171", "Main Library"))
        )
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(copyRepo.count()).isZero();
    }

    @Test
    void addThrowsWhenIsbnUnknown() {
        assertThatThrownBy(
                () -> new Inventory(copyRepo, bookRepo).
                        add(new Copy("unknown-isbn", "Main Library"))
        )
                .isInstanceOf(BookNotFoundException.class);

        assertThat(copyRepo.count()).isZero();
    }

    @Test
    void removeThrowsWhenIdMissing() {
        bookRepo.save(new Book("9780689315484", "Nighty-Nightmare", "Deborah and James Howe"));

        assertThatThrownBy(() -> new Inventory(copyRepo, bookRepo).remove(12345L))
                .isInstanceOf(CopyNotFoundException.class);

        assertThat(copyRepo.count()).isZero();
    }
}
