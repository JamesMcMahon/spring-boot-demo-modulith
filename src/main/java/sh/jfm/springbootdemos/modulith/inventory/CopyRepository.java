package sh.jfm.springbootdemos.modulith.inventory;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

interface CopyRepository extends CrudRepository<Copy, Long> {
    long countByIsbnAndAvailableTrue(String isbn);

    Optional<Copy> findFirstByIsbnAndAvailableTrue(String isbn);
}
