package sh.jfm.springbootdemos.modulith.data;

import org.springframework.data.repository.CrudRepository;
import sh.jfm.springbootdemos.modulith.model.Book;

import java.util.Optional;

public interface BookRepository extends CrudRepository<Book, Long> {
    boolean existsByIsbn(String isbn);

    Optional<Book> findByIsbn(String isbn);
}
