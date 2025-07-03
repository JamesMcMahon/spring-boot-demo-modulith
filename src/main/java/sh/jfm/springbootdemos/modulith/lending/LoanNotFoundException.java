package sh.jfm.springbootdemos.modulith.lending;

class LoanNotFoundException extends RuntimeException {
    LoanNotFoundException(long patronId, String isbn) {
        super("Active loan for patron %d and ISBN %s not found".formatted(patronId, isbn));
    }
}
