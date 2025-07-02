package sh.jfm.springbootdemos.modulith.lendingevents;

import org.springframework.context.ApplicationEvent;

import java.util.Objects;

/// Event that represents the return of a copy to the library.
/// This event is fired when a book copy is returned by a borrower.
public class ReturnCopyEvent extends ApplicationEvent {
    /// The ID of the copy being returned.
    private final Long copyId;

    /// Creates a new ReturnCopyEvent.
    ///
    /// @param source The source of the event
    /// @param copyId The ID of the copy being returned
    /// @throws NullPointerException if copyId is null
    public ReturnCopyEvent(Object source, Long copyId) {
        super(source);
        this.copyId = Objects.requireNonNull(copyId);
    }

    public Long getCopyId() {
        return copyId;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof ReturnCopyEvent that)) return false;

        return copyId.equals(that.copyId);
    }

    @Override
    public int hashCode() {
        return copyId.hashCode();
    }
}
