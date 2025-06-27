package sh.jfm.springbootdemos.modulith.services;

import sh.jfm.springbootdemos.modulith.controllers.RestExceptionAdvice;

/// Domain error raised by [Catalog] when a book already exists.
///
/// Propagates to REST where [RestExceptionAdvice]
/// maps it to an HTTP status.
public class BookAlreadyExistsException extends RuntimeException {
    public BookAlreadyExistsException(String isbn) {
        super("Book %s already exists".formatted(isbn));
    }
}
