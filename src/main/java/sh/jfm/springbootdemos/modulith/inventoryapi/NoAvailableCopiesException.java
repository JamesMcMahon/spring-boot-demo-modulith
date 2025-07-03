package sh.jfm.springbootdemos.modulith.inventoryapi;

public class NoAvailableCopiesException extends RuntimeException {
    public NoAvailableCopiesException(String isbn) {
        super("No available copies for ISBN %s".formatted(isbn));
    }
}
