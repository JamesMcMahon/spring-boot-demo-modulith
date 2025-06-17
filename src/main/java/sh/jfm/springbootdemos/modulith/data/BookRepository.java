package sh.jfm.springbootdemos.modulith.data;

import org.springframework.data.repository.CrudRepository;
import sh.jfm.springbootdemos.modulith.model.Book;

public interface BookRepository extends CrudRepository<Book, String> {
}
