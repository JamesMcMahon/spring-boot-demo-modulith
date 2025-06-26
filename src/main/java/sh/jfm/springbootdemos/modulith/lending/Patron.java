package sh.jfm.springbootdemos.modulith.lending;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("patrons")
public record Patron(@Id Long id) {
    public Patron() {
        this(null);
    }
}
