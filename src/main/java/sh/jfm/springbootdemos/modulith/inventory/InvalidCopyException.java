package sh.jfm.springbootdemos.modulith.inventory;

class InvalidCopyException extends RuntimeException {
    InvalidCopyException(String isbn) {
        super("ISBN %s not valid".formatted(isbn));
    }
}
