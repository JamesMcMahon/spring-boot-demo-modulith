package sh.jfm.springbootdemos.modulith.lending;

class PatronNotFoundException extends RuntimeException {
    PatronNotFoundException(long id) {
        super("Patron %d not found".formatted(id));
    }
}
