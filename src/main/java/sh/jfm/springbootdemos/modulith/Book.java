package sh.jfm.springbootdemos.modulith;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/// Domain object representing books in a catalog
@Table("books")
public record Book(
        @Id String isbn,
        String title,
        String author
) {
}
