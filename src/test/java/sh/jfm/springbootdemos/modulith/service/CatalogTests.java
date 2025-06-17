package sh.jfm.springbootdemos.modulith.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import sh.jfm.springbootdemos.modulith.data.BookRepository;
import sh.jfm.springbootdemos.modulith.model.Book;
import sh.jfm.springbootdemos.modulith.services.BookAlreadyExistsException;
import sh.jfm.springbootdemos.modulith.services.BookNotFoundException;
import sh.jfm.springbootdemos.modulith.services.Catalog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/// Tests [Catalog] with a real H2 DB (`@DataJdbcTest`) but
/// without the web layer for faster feedback.
@DataJdbcTest
class CatalogTests {

    @Autowired
    BookRepository repo;

    @Test
    void addInsertsNewBook() {
        var book = new Book("9780132350884", "Clean Code", "Robert C. Martin");

        new Catalog(repo).add(book);

        // verify no extra rows were written
        assertThat(repo.count()).isOne();
        assertThat(repo.findByIsbn(book.isbn()))
                .isPresent()
                .get()  // unwraps the optional
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(book);
    }

    @Test
    void addFailsIfThereIsAnId() {
        assertThatThrownBy(() -> new Catalog(repo).add(
                new Book(
                        5L,
                        "9780132350884",
                        "Clean Code",
                        "Robert C. Martin"
                )))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateChangesExistingBook() {
        var catalog = new Catalog(repo);
        var original = new Book("9780132350884", "Clean Code", "Robert C. Martin");
        catalog.add(original);

        var revised = new Book("9780132350884", "Cleaner Code", "Bob Martin");
        catalog.update(revised);

        // verify no extra rows were written
        assertThat(repo.count()).isOne();
        assertThat(repo.findByIsbn(revised.isbn()))
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(revised);
    }

    @Test
    void addThrowsWhenIsbnAlreadyExists() {
        var catalog = new Catalog(repo);
        var book = new Book("9780132350884", "Clean Code", "Robert C. Martin");
        catalog.add(book);

        assertThatThrownBy(() -> catalog.add(book))
                .isInstanceOf(BookAlreadyExistsException.class);

        // verify no extra rows were written
        assertThat(repo.count()).isOne();
    }

    @Test
    void updateThrowsWhenIsbnUnknown() {
        var catalog = new Catalog(repo);
        var unknown = new Book("9780132350884", "Clean Code", "Robert C. Martin");

        assertThatThrownBy(() -> catalog.update(unknown))
                .isInstanceOf(BookNotFoundException.class);

        // verify no extra rows were written
        assertThat(repo.count()).isZero();      // nothing was written
    }
}
