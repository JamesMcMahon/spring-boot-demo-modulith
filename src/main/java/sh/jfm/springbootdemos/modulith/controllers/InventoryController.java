package sh.jfm.springbootdemos.modulith.controllers;

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

    @GetMapping("/books/{isbn}/availability")
    Map<String, Long> availability(@PathVariable String isbn) {
        return Map.of("available", inventory.availability(isbn));
    }

    private record NewCopyRequest(String isbn, String location) {
    }

    @PostMapping("/copies")
    ResponseEntity<Copy> add(@RequestBody NewCopyRequest body) {
        var inserted = inventory.add(new Copy(body.isbn(), body.location()));
        return ResponseEntity
                .created(URI.create("/inventory/copies/" + inserted.id()))
                .body(inserted);
    }

    private record AvailabilityUpdateRequest(boolean available) {
    }

    @PatchMapping("/copies/{copyId}")
    ResponseEntity<Void> setAvailability(
            @PathVariable long copyId,
            @RequestBody AvailabilityUpdateRequest request
    ) {
        inventory.setAvailability(copyId, request.available());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/copies/{copyId}")
    ResponseEntity<Void> delete(@PathVariable long copyId) {
        inventory.remove(copyId);
        return ResponseEntity.noContent().build();
    }
}
