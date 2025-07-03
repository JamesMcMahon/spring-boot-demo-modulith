package sh.jfm.springbootdemos.modulith.inventory;

import org.springframework.stereotype.Component;
import sh.jfm.springbootdemos.modulith.inventoryapi.InventoryApi;

/// Simple direct call implementation of public api.
/// Component is injected at run time to avoid direct dependencies.
@Component
public class DirectInventoryApi implements InventoryApi {

    private final Inventory inventory;

    public DirectInventoryApi(Inventory inventory) {
        this.inventory = inventory;
    }

    public Long markNextCopyAsUnavailable(String isbn) {
        return inventory.markNextCopyAsUnavailable(isbn).id();
    }
}
