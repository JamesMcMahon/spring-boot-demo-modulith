package sh.jfm.springbootdemos.modulith.inventory;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CopyRepository extends CrudRepository<Copy, Long> {
    long countByIsbnAndAvailableTrue(String isbn);

    Optional<Copy> findFirstByIsbnAndAvailableTrue(String isbn);
}
