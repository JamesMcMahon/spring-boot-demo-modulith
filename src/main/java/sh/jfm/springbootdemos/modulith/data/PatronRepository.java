package sh.jfm.springbootdemos.modulith.data;

import org.springframework.data.repository.CrudRepository;
import sh.jfm.springbootdemos.modulith.model.Patron;

public interface PatronRepository extends CrudRepository<Patron, Long> {
}
