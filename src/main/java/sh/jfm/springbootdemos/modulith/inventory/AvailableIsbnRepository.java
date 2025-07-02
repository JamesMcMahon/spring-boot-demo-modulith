package sh.jfm.springbootdemos.modulith.inventory;

import org.springframework.data.repository.CrudRepository;

interface AvailableIsbnRepository extends CrudRepository<AvailableIsbn, Long> {
    boolean existsByIsbn(String isbn);
}
