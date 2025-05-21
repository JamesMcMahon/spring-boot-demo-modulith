package sh.jfm.springbootdemos.modulith.catalog.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJdbcTest
//@Import(CatalogJdbcConfiguration.class)
//@ActiveProfiles("test")
class BookRepositoryTest {

    @Autowired
    private BookRepository repository;

    @Test
    void shouldSaveAndFindBook() {
        // Given
        UUID id = UUID.randomUUID();
        Book book = new Book(id, "Domain-Driven Design", "Eric Evans", 5);

        // When
        Book savedBook = repository.save(book);
        Optional<Book> foundBook = repository.findById(id);

        // Then
        assertThat(savedBook).isNotNull();
        assertThat(foundBook).isPresent();
        
        Book retrievedBook = foundBook.get();
        assertEquals(id, retrievedBook.getId());
        assertEquals("Domain-Driven Design", retrievedBook.getTitle());
        assertEquals("Eric Evans", retrievedBook.getAuthor());
        assertEquals(5, retrievedBook.getCopiesAvailable());
    }

    @Test
    void shouldUpdateExistingBook() {
        // Given
        UUID id = UUID.randomUUID();
        Book book = new Book(id, "Clean Code", "Robert C. Martin", 3);
        repository.save(book);

        // When
        Book savedBook = repository.findById(id).get();
        savedBook.setTitle("Clean Code: A Handbook of Agile Software Craftsmanship");
        savedBook.addCopies(2);
        repository.save(savedBook);

        // Then
        Book updatedBook = repository.findById(id).get();
        assertEquals("Clean Code: A Handbook of Agile Software Craftsmanship", updatedBook.getTitle());
        assertEquals(5, updatedBook.getCopiesAvailable());
    }

    @Test
    void shouldDeleteBook() {
        // Given
        UUID id = UUID.randomUUID();
        Book book = new Book(id, "Refactoring", "Martin Fowler", 2);
        repository.save(book);

        // When
        repository.deleteById(id);

        // Then
        assertFalse(repository.existsById(id));
    }

    @Test
    void shouldHandleBookOperationsWithPersistence() {
        // Given
        UUID id = UUID.randomUUID();
        Book book = new Book(id, "Test-Driven Development", "Kent Beck", 1);
        repository.save(book);

        // When - Borrow the book
        Book savedBook = repository.findById(id).get();
        savedBook.borrow();
        repository.save(savedBook);
        
        // Then - Verify book was borrowed
        Book borrowedBook = repository.findById(id).get();
        assertEquals(0, borrowedBook.getCopiesAvailable());
        
        // When - Return the book
        borrowedBook.returnCopy();
        repository.save(borrowedBook);
        
        // Then - Verify book was returned
        Book returnedBook = repository.findById(id).get();
        assertEquals(1, returnedBook.getCopiesAvailable());
    }
    
    @Test
    void shouldCreateBookWithBookIdAndFindItWithUUID() {
        // Given
        UUID bookId = UUID.randomUUID();
        Book book = new Book(bookId, "Working Effectively with Legacy Code", "Michael Feathers", 3);
        
        // When
        Book savedBook = repository.save(book);
        
        // Then
        Optional<Book> foundBook = repository.findById(bookId);
        assertTrue(foundBook.isPresent());
        assertEquals(bookId, foundBook.get().getId());
    }
}
