package sh.jfm.springbootdemos.modulith.lending;

class NoAvailableCopiesException extends RuntimeException {
    NoAvailableCopiesException(String isbn) {
        super("No available copies for ISBN %s".formatted(isbn));
    }
}
