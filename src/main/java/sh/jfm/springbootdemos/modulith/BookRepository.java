package sh.jfm.springbootdemos.modulith;

import org.springframework.data.repository.CrudRepository;

public interface BookRepository extends CrudRepository<Book, String> {
}
