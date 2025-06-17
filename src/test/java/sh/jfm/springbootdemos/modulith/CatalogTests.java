package sh.jfm.springbootdemos.modulith;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/// Tests [Catalog] with a real H2 DB (`@DataJdbcTest`) but
/// without the web layer for faster feedback.
@DataJdbcTest
class CatalogTests {

    @Autowired
    JdbcAggregateTemplate template;
    @Autowired
    BookRepository repo;

    @Test
    void addInsertsNewBook() {
        var book = new Book("9780132350884", "Clean Code", "Robert C. Martin");

        new Catalog(repo, template).add(book);

        // verify no extra rows were written
        assertThat(repo.count()).isOne();
        assertThat(repo.findById(book.isbn()))
                .contains(book);
    }

    @Test
    void updateChangesExistingBook() {
        var catalog = new Catalog(repo, template);
        var original = new Book("9780132350884", "Clean Code", "Robert C. Martin");
        catalog.add(original);

        var revised = new Book("9780132350884", "Cleaner Code", "Bob Martin");
        catalog.update(revised);

        // verify no extra rows were written
        assertThat(repo.count()).isOne();
        assertThat(repo.findById(revised.isbn()))
                .contains(revised);
    }

    @Test
    void addThrowsWhenIsbnAlreadyExists() {
        var catalog = new Catalog(repo, template);
        var book = new Book("9780132350884", "Clean Code", "Robert C. Martin");
        catalog.add(book);

        assertThatThrownBy(() -> catalog.add(book))
                .isInstanceOf(BookAlreadyExistsException.class);

        // verify no extra rows were written
        assertThat(repo.count()).isOne();
    }

    @Test
    void updateThrowsWhenIsbnUnknown() {
        var catalog = new Catalog(repo, template);
        var unknown = new Book("9780132350884", "Clean Code", "Robert C. Martin");

        assertThatThrownBy(() -> catalog.update(unknown))
                .isInstanceOf(BookNotFoundException.class);

        // verify no extra rows were written
        assertThat(repo.count()).isZero();      // nothing was written
    }
}
