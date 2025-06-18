package sh.jfm.springbootdemos.modulith.services;

/// Raised when a copy id cannot be resolved
public class CopyNotFoundException extends RuntimeException {
    public CopyNotFoundException(long id) {
        super("Copy %d not found".formatted(id));
    }
}
