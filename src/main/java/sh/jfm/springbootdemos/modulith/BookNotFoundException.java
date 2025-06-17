package sh.jfm.springbootdemos.modulith;

/// Domain error raised by [Catalog] when a book cannot be found.
///
/// Propagates to REST where [RestExceptionAdvice]
/// maps it to an HTTP status.
public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(String isbn) {
        super("Book %s not found".formatted(isbn));
    }
}
