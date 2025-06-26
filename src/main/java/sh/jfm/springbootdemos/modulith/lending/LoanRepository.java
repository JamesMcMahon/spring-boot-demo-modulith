package sh.jfm.springbootdemos.modulith.lending;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface LoanRepository extends CrudRepository<Loan, Long> {

    List<Loan> findByPatronId(long patronId);

    Optional<Loan> findByPatronIdAndIsbn(long patronId, String isbn);
}
