package sh.jfm.springbootdemos.modulith.catalog;

/// Domain error raised by [Catalog] when a book cannot be found.
///
/// Propagates to REST where [CatalogRestExceptionAdvice]
/// maps it to an HTTP status.
class BookNotFoundException extends RuntimeException {
    BookNotFoundException(String isbn) {
        super("Book %s not found".formatted(isbn));
    }
}
