package sh.jfm.springbootdemos.modulith.http;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import sh.jfm.springbootdemos.modulith.catalog.BookAlreadyExistsException;
import sh.jfm.springbootdemos.modulith.catalog.BookNotFoundException;
import sh.jfm.springbootdemos.modulith.inventory.CopyNotFoundException;
import sh.jfm.springbootdemos.modulith.inventory.InvalidCopyException;
import sh.jfm.springbootdemos.modulith.inventory.NoAvailableCopiesException;
import sh.jfm.springbootdemos.modulith.lending.LoanNotFoundException;
import sh.jfm.springbootdemos.modulith.lending.PatronNotFoundException;

import static org.springframework.http.HttpStatus.*;

/// Centralizes translation of domain exceptions to HTTP status codes,
/// avoiding scattered try/catch in controllers.
@ControllerAdvice
public class RestExceptionAdvice {

    @ExceptionHandler(BookAlreadyExistsException.class)
    ResponseEntity<Void> handleBookExists() {
        return ResponseEntity.status(CONFLICT).build();
    }

    @ExceptionHandler(BookNotFoundException.class)
    ResponseEntity<Void> handleBookMissing() {
        return ResponseEntity.status(NOT_FOUND).build();
    }

    @ExceptionHandler(CopyNotFoundException.class)
    ResponseEntity<Void> handleCopyMissing() {
        return ResponseEntity.status(NOT_FOUND).build();
    }

    @ExceptionHandler(InvalidCopyException.class)
    ResponseEntity<Void> handleInvalidCopy() {
        return ResponseEntity.status(BAD_REQUEST).build();
    }

    @ExceptionHandler(NoAvailableCopiesException.class)
    ResponseEntity<Void> handleNoAvailableCopies() {
        return ResponseEntity.status(CONFLICT).build();
    }

    @ExceptionHandler(PatronNotFoundException.class)
    ResponseEntity<Void> handlePatronMissing() {
        return ResponseEntity.status(NOT_FOUND).build();
    }

    @ExceptionHandler(LoanNotFoundException.class)
    ResponseEntity<Void> handleLoanNotFound() {
        return ResponseEntity.status(CONFLICT).build();
    }
}
