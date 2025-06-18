package sh.jfm.springbootdemos.modulith.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/// Physical copy of a catalog book
@Table("copies")
public record Copy(
        @Id Long id,
        String isbn,
        String location
) {
    public Copy(String isbn, String location) {
        this(null, isbn, location);
    }
}
