package sh.jfm.springbootdemos.modulith.lending;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

/// REST adapter for [Lending]
@RestController
@RequestMapping("/lending")
class LendingController {

    private final Lending lending;

    LendingController(Lending lending) {
        this.lending = lending;
    }

    private record BorrowRequest(Long patronId, String isbn) {
    }

    @PostMapping("/loans")
    ResponseEntity<Loan> borrow(@RequestBody BorrowRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(lending.borrow(request.patronId(), request.isbn()));
    }

    @PostMapping("/returns")
    ResponseEntity<Void> returnBook(@RequestBody BorrowRequest request) {
        lending.returnBook(request.patronId(), request.isbn());
        return ResponseEntity.noContent().build();
    }

    private record NewPatronRequest(String firstName, String lastName) {
    }

    @PostMapping("/patrons")
    ResponseEntity<Patron> createPatron(@RequestBody NewPatronRequest body) {
        var created = lending.addPatron(new Patron(body.firstName(), body.lastName()));
        return ResponseEntity
                .created(URI.create("/lending/patrons/" + created.id()))
                .body(created);
    }

    @GetMapping("/patrons/{patronId}/loans")
    Map<String, List<Loan>> loansForPatron(@PathVariable long patronId) {
        return Map.of("loans", lending.findLoansForPatron(patronId));
    }
}
