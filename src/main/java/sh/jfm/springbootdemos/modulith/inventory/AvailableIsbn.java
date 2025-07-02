package sh.jfm.springbootdemos.modulith.inventory;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("available_isbns")
public record AvailableIsbn(@Id Long id, String isbn) {
    public AvailableIsbn(String isbn) {
        this(null, isbn);
    }
}
