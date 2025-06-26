package sh.jfm.springbootdemos.modulith.lending;

import org.springframework.data.repository.CrudRepository;

public interface PatronRepository extends CrudRepository<Patron, Long> {
}
