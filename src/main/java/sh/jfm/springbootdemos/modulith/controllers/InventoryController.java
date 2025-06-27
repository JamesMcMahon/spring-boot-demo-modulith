package sh.jfm.springbootdemos.modulith.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sh.jfm.springbootdemos.modulith.model.Copy;
import sh.jfm.springbootdemos.modulith.services.Inventory;

import java.net.URI;
import java.util.Map;

/// REST adapter for [Inventory]
@RestController
@RequestMapping("/inventory")
class InventoryController {

    private final Inventory inventory;

    InventoryController(Inventory inventory) {
        this.inventory = inventory;
    }

    @Operation(
            summary = "Get number of available copies",
            tags = {"05 Inventory – Check Availability"}
    )
    @GetMapping("/books/{isbn}/availability")
    Map<String, Long> availability(@Parameter(description = "ISBN of the book") @PathVariable String isbn) {
        return Map.of("available", inventory.availability(isbn));
    }

    private record NewCopyRequest(String isbn, String location) {
        Copy toCopy() {
            return new Copy(isbn, location);
        }
    }

    @Operation(
            summary = "Register a new copy of a book",
            tags = {"04 Inventory – Add Copy"}
    )
    @PostMapping("/copies")
    ResponseEntity<Copy> add(@RequestBody NewCopyRequest newCopy) {
        var inserted = inventory.add(newCopy.toCopy());
        return ResponseEntity
                .created(URI.create("/inventory/copies/" + inserted.id()))
                .body(inserted);
    }
}
