package sh.jfm.springbootdemos.modulith.lending;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("patrons")
public record Patron(
        @Id Long id,
        String firstName,
        String lastName
) {
    public Patron(String firstName, String lastName) {
        this(null, firstName, lastName);
    }
}
