package sh.jfm.springbootdemos.modulith.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import sh.jfm.springbootdemos.modulith.services.*;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

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

    @ExceptionHandler(PatronNotFoundException.class)
    ResponseEntity<Void> handlePatronMissing() {
        return ResponseEntity.status(NOT_FOUND).build();
    }

    @ExceptionHandler({NoAvailableCopiesException.class, LoanNotFoundException.class})
    ResponseEntity<Void> handleLoanConflicts() {
        return ResponseEntity.status(CONFLICT).build();
    }
}
