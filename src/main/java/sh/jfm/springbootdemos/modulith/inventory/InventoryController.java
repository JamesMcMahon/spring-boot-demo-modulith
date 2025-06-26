package sh.jfm.springbootdemos.modulith.inventory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
