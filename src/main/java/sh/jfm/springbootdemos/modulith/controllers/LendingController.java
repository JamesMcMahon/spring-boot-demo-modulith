package sh.jfm.springbootdemos.modulith.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sh.jfm.springbootdemos.modulith.model.Loan;
import sh.jfm.springbootdemos.modulith.services.Lending;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/patrons/{patronId}/loans")
    Map<String, List<Loan>> loansForPatron(@PathVariable long patronId) {
        return Map.of("loans", lending.findLoansForPatron(patronId));
    }
}
