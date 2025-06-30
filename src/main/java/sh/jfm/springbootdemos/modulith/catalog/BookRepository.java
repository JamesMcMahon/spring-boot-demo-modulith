package sh.jfm.springbootdemos.modulith.catalog;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

interface BookRepository extends CrudRepository<Book, Long> {
    boolean existsByIsbn(String isbn);

    Optional<Book> findByIsbn(String isbn);
}
