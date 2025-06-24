package sh.jfm.springbootdemos.modulith.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import sh.jfm.springbootdemos.modulith.data.BookRepository;
import sh.jfm.springbootdemos.modulith.model.Book;

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
        var book = new Book("9780132350884", "Clean Code", "Robert C. Martin");

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
        catalog.add(new Book("9780132350884", "Clean Code", "Robert C. Martin"));

        var revised = new Book("9780132350884", "Cleaner Code", "Bob Martin");
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
                        "9780132350884",
                        "Clean Code",
                        "Robert C. Martin"
                )))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addThrowsWhenIsbnAlreadyExists() {
        var book = new Book("9780132350884", "Clean Code", "Robert C. Martin");
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
                        "9780132350884",
                        "Clean Code",
                        "Robert C. Martin"
                )))
                .isInstanceOf(BookNotFoundException.class);
        // verify no extra rows were written
        assertThat(repo.count()).isZero();
    }
}
