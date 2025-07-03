package sh.jfm.springbootdemos.modulith.lending;

public class NoAvailableCopiesException extends RuntimeException {
    public NoAvailableCopiesException(String isbn) {
        super("No available copies for ISBN %s".formatted(isbn));
    }
}
