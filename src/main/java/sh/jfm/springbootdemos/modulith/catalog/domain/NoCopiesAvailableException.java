package sh.jfm.springbootdemos.modulith.catalog.domain;

import java.util.UUID;

/**
 * Exception thrown when attempting to borrow a book with no copies available.
 */
public class NoCopiesAvailableException extends RuntimeException {

    public NoCopiesAvailableException(UUID bookId) {
        super("No copies available for book: " + bookId);
    }
}
