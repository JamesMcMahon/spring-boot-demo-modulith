package sh.jfm.springbootdemos.modulith;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/// Centralizes translation of domain exceptions to HTTP status codes,
/// avoiding scattered try/catch in controllers.
@ControllerAdvice
public class RestExceptionAdvice {

    @ExceptionHandler(BookAlreadyExistsException.class)
    ResponseEntity<Void> handleBookExists() {
        return ResponseEntity.status(409).build();
    }

    @ExceptionHandler(BookNotFoundException.class)
    ResponseEntity<Void> handleBookMissing() {
        return ResponseEntity.status(404).build();
    }
}
