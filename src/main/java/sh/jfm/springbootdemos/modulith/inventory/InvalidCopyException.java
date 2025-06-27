package sh.jfm.springbootdemos.modulith.inventory;

public class InvalidCopyException extends RuntimeException {
    public InvalidCopyException(String isbn) {
        super("ISBN %s not valid".formatted(isbn));
    }
}
