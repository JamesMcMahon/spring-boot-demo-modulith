package sh.jfm.springbootdemos.modulith.data;

import org.springframework.data.repository.CrudRepository;
import sh.jfm.springbootdemos.modulith.model.Copy;

import java.util.Optional;

public interface CopyRepository extends CrudRepository<Copy, Long> {
    long countByIsbnAndAvailableTrue(String isbn);

    Optional<Copy> findFirstByIsbnAndAvailableTrue(String isbn);
}
