package sh.jfm.springbootdemos.modulith.inventoryapi;

import java.util.Optional;

/// Interface to abstract away public api
public interface InventoryApi {

    /// Marks the next available copy of a book as unavailable in the inventory.
    ///
    /// @param isbn The ISBN (International Standard Book Number) of the book
    /// @return The ID of the copy that was marked as unavailable, empty Optional if none available
    Optional<Long> markNextCopyAsUnavailable(String isbn);
}
