package sh.jfm.springbootdemos.modulith.catalog;

/// Domain error raised by [Catalog] when a book already exists.
///
/// Propagates to REST where [CatalogRestExceptionAdvice]
/// maps it to an HTTP status.
class BookAlreadyExistsException extends RuntimeException {
    BookAlreadyExistsException(String isbn) {
        super("Book %s already exists".formatted(isbn));
    }
}
