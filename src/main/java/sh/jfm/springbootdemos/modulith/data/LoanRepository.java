package sh.jfm.springbootdemos.modulith.data;

import org.springframework.data.repository.CrudRepository;
import sh.jfm.springbootdemos.modulith.model.Loan;

import java.util.List;
import java.util.Optional;

public interface LoanRepository extends CrudRepository<Loan, Long> {

    List<Loan> findByPatronId(long patronId);

    Optional<Loan> findByPatronIdAndIsbn(long patronId, String isbn);
}
