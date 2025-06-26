package sh.jfm.springbootdemos.modulith.lending;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table("loans")
public record Loan(@Id Long id,
                   Long copyId,
                   String isbn,
                   Long patronId,
                   LocalDate dueDate) {
    public Loan(Long copyId, String isbn, Long patronId, LocalDate dueDate) {
        this(null, copyId, isbn, patronId, dueDate);
    }
}
