package sh.jfm.springbootdemos.modulith.inventory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class InventoryExceptionAdvice {

    @ExceptionHandler(InvalidCopyException.class)
    ResponseEntity<Void> handleInvalidCopy() {
        return ResponseEntity.status(BAD_REQUEST).build();
    }
}
