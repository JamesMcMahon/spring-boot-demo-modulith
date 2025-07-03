package sh.jfm.springbootdemos.modulith.inventoryapi;

/// Interface to abstract away public api
public interface InventoryApi {

    /// Marks the next available copy of a book as unavailable in the inventory.
    ///
    /// @param isbn The ISBN (International Standard Book Number) of the book
    /// @return The ID of the copy that was marked as unavailable
    /// @throws NoAvailableCopiesException if there are no available copies
    Long markNextCopyAsUnavailable(String isbn);
}
