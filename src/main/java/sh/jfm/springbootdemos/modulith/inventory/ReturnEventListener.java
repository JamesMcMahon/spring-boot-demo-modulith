package sh.jfm.springbootdemos.modulith.inventory;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import sh.jfm.springbootdemos.modulith.lendingevents.ReturnCopyEvent;

/// Event listener component that handles return events for copies.
/// This listener responds to ReturnCopyEvent events and updates the inventory
/// to mark returned copies as available.
@Component
class ReturnEventListener {

    private final Inventory inventory;

    ReturnEventListener(Inventory inventory) {
        this.inventory = inventory;
    }

    @EventListener
    public void handleReturnEvent(ReturnCopyEvent event) {
        inventory.markAsAvailable(event.getCopyId());
    }
}
