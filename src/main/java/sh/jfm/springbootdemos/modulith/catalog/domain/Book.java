package sh.jfm.springbootdemos.modulith.catalog.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;
import java.util.UUID;

/**
 * Book aggregate that represents a book in the library catalog.
 * It contains book metadata and tracks available copies for borrowing.
 */
public class Book {
    @Id
    private UUID id;
    private String title;
    private String author;
    
    private int copiesAvailable;

    /**
     * Creates a new book with the provided details.
     *
     * @param id              the book's unique identifier
     * @param title           the book's title
     * @param author          the book's author
     * @param copiesAvailable the number of copies available for borrowing
     */
    @PersistenceCreator
    public Book(UUID id, String title, String author, int copiesAvailable) {
        this.id = Objects.requireNonNull(id, "Book ID cannot be null");
        this.title = Objects.requireNonNull(title, "Title cannot be null");
        this.author = Objects.requireNonNull(author, "Author cannot be null");
        
        if (copiesAvailable < 0) {
            throw new IllegalArgumentException("Copies available cannot be negative");
        }
        
        this.copiesAvailable = copiesAvailable;
    }

    /**
     * Adds copies to the book's available inventory.
     *
     * @param copies the number of copies to add
     * @throws IllegalArgumentException if the number of copies is not positive
     */
    public void addCopies(int copies) {
        if (copies <= 0) {
            throw new IllegalArgumentException("Number of copies to add must be positive");
        }
        
        this.copiesAvailable += copies;
    }

    /**
     * Borrows one copy of the book, reducing the available inventory.
     *
     * @throws NoCopiesAvailableException if no copies are available for borrowing
     */
    public void borrow() {
        if (copiesAvailable <= 0) {
            throw new NoCopiesAvailableException(getId());
        }
        
        this.copiesAvailable--;
    }

    /**
     * Returns one copy of the book, increasing the available inventory.
     */
    public void returnCopy() {
        this.copiesAvailable++;
    }

    // Getters

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = Objects.requireNonNull(title, "Title cannot be null");
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = Objects.requireNonNull(author, "Author cannot be null");
    }

    public int getCopiesAvailable() {
        return copiesAvailable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(id, book.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", copiesAvailable=" + copiesAvailable +
                '}';
    }
}
