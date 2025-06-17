package sh.jfm.springbootdemos.modulith.services;

/// Domain error raised by [Catalog] when a book already exists.
///
/// Propagates to REST where [sh.jfm.springbootdemos.modulith.controllers.RestExceptionAdvice]
/// maps it to an HTTP status.
public class BookAlreadyExistsException extends RuntimeException {
    public BookAlreadyExistsException(String isbn) {
        super("Book %s already exists".formatted(isbn));
    }
}
