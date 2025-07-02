package sh.jfm.springbootdemos.modulith.inventory;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import sh.jfm.springbootdemos.modulith.catalogevents.BookAddedEvent;

@Component
class BookAddedEventListener {

    private final Inventory inventory;

    BookAddedEventListener(Inventory inventory) {
        this.inventory = inventory;
    }

    @EventListener
    void handleBookAddedEvent(BookAddedEvent event) {
        inventory.registerIsbn(event.getIsbn());
    }
}
