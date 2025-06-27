package sh.jfm.springbootdemos.modulith.catalog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/// Tests [Catalog] with a real H2 DB (`@DataJdbcTest`) but
/// without the web layer for faster feedback.
@DataJdbcTest
class CatalogTests {

    @Autowired
    BookRepository repo;

    private Catalog catalog;

    @BeforeEach
    void setUp() {
        catalog = new Catalog(repo);
    }

    @Test
    void addInsertsNewBook() {
        var book = new Book("9780439706230", "Out From Boneville", "Jeff Smith");

        catalog.add(book);

        assertThat(repo.count()).isOne();
        assertThat(repo.findByIsbn(book.isbn()))
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(book);
    }

    @Test
    void updateChangesExistingBook() {
        catalog.add(new Book("0963660985", "The Great Cow Race", "Jeff Smith"));

        var revised = new Book("0963660985", "The Greater Cow Race", "Jeff Smith");
        catalog.update(revised);

        assertThat(repo.count()).isOne();
        assertThat(repo.findByIsbn(revised.isbn()))
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(revised);
    }

    @Test
    void addFailsIfThereIsAnId() {
        assertThatThrownBy(() -> catalog.add(
                new Book(
                        5L,
                        "1888963034",
                        "Rock Jaw: Master of the Eastern Border",
                        "Jeff Smith"
                )))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addThrowsWhenIsbnAlreadyExists() {
        var book = new Book("9780439706254", "The Dragonslayer", "Jeff Smith");
        catalog.add(book);

        assertThatThrownBy(() -> catalog.add(book))
                .isInstanceOf(BookAlreadyExistsException.class);
        // verify no extra rows were written
        assertThat(repo.count()).isOne();
    }


    @Test
    void updateThrowsWhenIsbnUnknown() {
        assertThatThrownBy(() -> catalog.update(
                new Book(
                        "0963660977",
                        "Eyes of the Storm",
                        "Jeff Smith"
                )))
                .isInstanceOf(BookNotFoundException.class);
        // verify no extra rows were written
        assertThat(repo.count()).isZero();
    }

    @Test
    void byIsbnFindsBooks() {
        var isbn = "1888963050";
        var addedBook = catalog.add(new Book(isbn, "Old Man's Cave", "Jeff Smith"));

        assertThat(catalog.byIsbn(isbn))
                .isPresent()
                .get()
                .isEqualTo(addedBook);
    }

    @Test
    void byIsbnReturnsEmptyWhenIsbnUnknown() {
        assertThat(catalog.byIsbn("unknown-isbn")).isNotPresent();
    }

    @Test
    void existsByIsbnReturnsIfBooksExists() {
        catalog.add(new Book("9780545135436", "Rose", "Jeff Smith"));
        assertThat(catalog.existsByIsbn("9780545135436")).isTrue();

        assertThat(catalog.existsByIsbn("unknown-isbn")).isFalse();
    }
}
