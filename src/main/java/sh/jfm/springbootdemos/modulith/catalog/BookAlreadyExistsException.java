package sh.jfm.springbootdemos.modulith.catalog;

/// Domain error raised by [Catalog] when a book already exists.
///
/// Propagates to REST where [sh.jfm.springbootdemos.modulith.http.RestExceptionAdvice]
/// maps it to an HTTP status.
public class BookAlreadyExistsException extends RuntimeException {
    public BookAlreadyExistsException(String isbn) {
        super("Book %s already exists".formatted(isbn));
    }
}
