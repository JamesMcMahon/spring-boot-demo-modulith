package sh.jfm.springbootdemos.modulith.lending;

import org.springframework.data.repository.CrudRepository;

interface PatronRepository extends CrudRepository<Patron, Long> {
}
