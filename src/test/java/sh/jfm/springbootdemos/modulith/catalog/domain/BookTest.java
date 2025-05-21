package sh.jfm.springbootdemos.modulith.catalog.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    @Test
    void createBookSuccessfully() {
        // Given
        UUID bookId = UUID.randomUUID();
        String title = "Clean Code";
        String author = "Robert C. Martin";
        int copiesAvailable = 5;

        // When
        Book book = new Book(bookId, title, author, copiesAvailable);

        // Then
        assertEquals(bookId, book.getId());
        assertEquals(title, book.getTitle());
        assertEquals(author, book.getAuthor());
        assertEquals(copiesAvailable, book.getCopiesAvailable());
    }

    @Test
    void shouldThrowExceptionForNullParameters() {
        // Given
        UUID bookId = UUID.randomUUID();
        String title = "Clean Code";
        String author = "Robert C. Martin";
        int copiesAvailable = 5;

        // Then
        assertThrows(NullPointerException.class, () -> new Book(null, title, author, copiesAvailable));
        assertThrows(NullPointerException.class, () -> new Book(bookId, null, author, copiesAvailable));
        assertThrows(NullPointerException.class, () -> new Book(bookId, title, null, copiesAvailable));
    }

    @Test
    void shouldThrowExceptionForNegativeCopies() {
        // Given
        UUID bookId = UUID.randomUUID();
        String title = "Clean Code";
        String author = "Robert C. Martin";
        int copiesAvailable = -1;

        // Then
        assertThrows(IllegalArgumentException.class, () -> new Book(bookId, title, author, copiesAvailable));
    }

    @Test
    void shouldAddCopiesSuccessfully() {
        // Given
        Book book = new Book(UUID.randomUUID(), "Clean Code", "Robert C. Martin", 5);
        int initialCopies = book.getCopiesAvailable();
        int copiesToAdd = 3;

        // When
        book.addCopies(copiesToAdd);

        // Then
        assertEquals(initialCopies + copiesToAdd, book.getCopiesAvailable());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})
    void shouldThrowExceptionWhenAddingNonPositiveCopies(int copiesToAdd) {
        // Given
        Book book = new Book(UUID.randomUUID(), "Clean Code", "Robert C. Martin", 5);

        // Then
        assertThrows(IllegalArgumentException.class, () -> book.addCopies(copiesToAdd));
    }

    @Test
    void shouldBorrowSuccessfully() {
        // Given
        Book book = new Book(UUID.randomUUID(), "Clean Code", "Robert C. Martin", 5);
        int initialCopies = book.getCopiesAvailable();

        // When
        book.borrow();

        // Then
        assertEquals(initialCopies - 1, book.getCopiesAvailable());
    }

    @Test
    void shouldThrowExceptionWhenBorrowingUnavailableBook() {
        // Given
        Book book = new Book(UUID.randomUUID(), "Clean Code", "Robert C. Martin", 0);

        // Then
        NoCopiesAvailableException exception = assertThrows(NoCopiesAvailableException.class, book::borrow);
        assertTrue(exception.getMessage().contains(book.getId().toString()));
    }

    @Test
    void shouldReturnCopySuccessfully() {
        // Given
        Book book = new Book(UUID.randomUUID(), "Clean Code", "Robert C. Martin", 5);
        int initialCopies = book.getCopiesAvailable();

        // When
        book.returnCopy();

        // Then
        assertEquals(initialCopies + 1, book.getCopiesAvailable());
    }

    @Test
    void testBookEquality() {
        // Given
        UUID bookId = UUID.randomUUID();
        Book book1 = new Book(bookId, "Clean Code", "Robert C. Martin", 5);
        Book book2 = new Book(bookId, "Different Title", "Different Author", 10);
        Book book3 = new Book(UUID.randomUUID(), "Clean Code", "Robert C. Martin", 5);

        // Then
        assertEquals(book1, book2, "Books with same ID should be equal regardless of other attributes");
        assertNotEquals(book1, book3, "Books with different IDs should not be equal");
    }

    @Test
    void borrowAndReturnScenario() {
        // Given
        Book book = new Book(UUID.randomUUID(), "Clean Code", "Robert C. Martin", 1);
        
        // When
        book.borrow(); // Now 0 copies
        
        // Then
        assertEquals(0, book.getCopiesAvailable());
        assertThrows(NoCopiesAvailableException.class, book::borrow);
        
        // When
        book.returnCopy(); // Now 1 copy
        
        // Then
        assertEquals(1, book.getCopiesAvailable());
        book.borrow(); // Should work now
        assertEquals(0, book.getCopiesAvailable());
    }
}
