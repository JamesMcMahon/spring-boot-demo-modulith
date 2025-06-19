package sh.jfm.springbootdemos.modulith.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("patrons")
public record Patron(@Id Long id) {
}
