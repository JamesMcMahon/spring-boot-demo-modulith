package sh.jfm.springbootdemos.modulith.services;

public class LoanNotFoundException extends RuntimeException {
    public LoanNotFoundException(long patronId, String isbn) {
        super("Active loan for patron %d and ISBN %s not found".formatted(patronId, isbn));
    }
}
