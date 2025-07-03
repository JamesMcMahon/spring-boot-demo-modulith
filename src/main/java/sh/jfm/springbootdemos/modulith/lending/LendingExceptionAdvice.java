package sh.jfm.springbootdemos.modulith.lending;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class LendingExceptionAdvice {

    @ExceptionHandler(PatronNotFoundException.class)
    ResponseEntity<Void> handlePatronMissing() {
        return ResponseEntity.status(NOT_FOUND).build();
    }

    @ExceptionHandler(LoanNotFoundException.class)
    ResponseEntity<Void> handleLoanMissing() {
        return ResponseEntity.status(CONFLICT).build();
    }

    @ExceptionHandler(NoAvailableCopiesException.class)
    ResponseEntity<Void> handleNoFreeCopies() {
        return ResponseEntity.status(CONFLICT).build();
    }
}
