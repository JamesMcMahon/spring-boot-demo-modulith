package sh.jfm.springbootdemos.modulith.catalogevents;

import org.springframework.context.ApplicationEvent;

import java.util.Objects;

/// Published right after a book has been stored in the catalog.
public class BookAddedEvent extends ApplicationEvent {

    private final String isbn;

    public BookAddedEvent(Object source, String isbn) {
        super(source);
        this.isbn = Objects.requireNonNull(isbn);
    }

    public String getIsbn() {
        return isbn;
    }

    @Override
    public final boolean equals(Object o) {
        return o instanceof BookAddedEvent that && isbn.equals(that.isbn);
    }

    @Override
    public int hashCode() {
        return isbn.hashCode();
    }
}
