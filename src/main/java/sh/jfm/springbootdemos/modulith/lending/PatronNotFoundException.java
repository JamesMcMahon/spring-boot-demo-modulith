package sh.jfm.springbootdemos.modulith.lending;

public class PatronNotFoundException extends RuntimeException {
    public PatronNotFoundException(long id) {
        super("Patron %d not found".formatted(id));
    }
}
