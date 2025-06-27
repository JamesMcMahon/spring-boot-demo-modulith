package sh.jfm.springbootdemos.modulith.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/// Domain object representing books in a catalog
@Table("books")
public record Book(
        @Id
        // ensure that the id is never sent to clients
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        Long id,
        String isbn,
        String title,
        String author
) {
    public Book(String isbn, String title, String author) {
        this(null, isbn, title, author);
    }
}
