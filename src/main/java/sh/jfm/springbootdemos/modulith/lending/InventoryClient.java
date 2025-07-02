package sh.jfm.springbootdemos.modulith.lending;

import org.springframework.stereotype.Component;
import sh.jfm.springbootdemos.modulith.inventory.Copy;
import sh.jfm.springbootdemos.modulith.inventory.Inventory;

@Component
public class InventoryClient {

    private final Inventory inventory;

    public InventoryClient(Inventory inventory) {
        this.inventory = inventory;
    }

    public Copy markNextCopyAsUnavailable(String isbn) {
        return inventory.markNextCopyAsUnavailable(isbn);
    }
}
