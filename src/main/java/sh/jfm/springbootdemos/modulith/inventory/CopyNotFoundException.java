package sh.jfm.springbootdemos.modulith.inventory;

/// Raised when a copy id cannot be resolved
class CopyNotFoundException extends RuntimeException {
    CopyNotFoundException(long id) {
        super("Copy %d not found".formatted(id));
    }
}
