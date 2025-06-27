package sh.jfm.springbootdemos.modulith.lending;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @Operation(
            summary = "Borrow a copy of a book",
            tags = {"07 Lending – Borrow Book"}
    )
    @PostMapping("/loans")
    ResponseEntity<Loan> borrow(@RequestBody BorrowRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(lending.borrow(request.patronId(), request.isbn()));
    }

    @Operation(
            summary = "Return a borrowed book",
            tags = {"08 Lending – Return Book"}
    )
    @PostMapping("/returns")
    ResponseEntity<Void> returnBook(@RequestBody BorrowRequest request) {
        lending.returnBook(request.patronId(), request.isbn());
        return ResponseEntity.noContent().build();
    }

    private record NewPatronRequest(String firstName, String lastName) {
        Patron toPatron() {
            return new Patron(firstName, lastName);
        }
    }

    @Operation(
            summary = "Create a new patron",
            tags = {"06 Lending – Create Patron"}
    )
    @PostMapping("/patrons")
    ResponseEntity<Patron> createPatron(@RequestBody NewPatronRequest newPatron) {
        var created = lending.addPatron(newPatron.toPatron());
        return ResponseEntity
                .created(URI.create("/lending/patrons/" + created.id()))
                .body(created);
    }

    @Operation(
            summary = "List loans for a patron",
            tags = {"09 Lending – List Loans"}
    )
    @GetMapping("/patrons/{patronId}/loans")
    Map<String, List<Loan>> loansForPatron(@Parameter(description = "Patron identifier") @PathVariable long patronId) {
        return Map.of("loans", lending.findLoansForPatron(patronId));
    }
}
