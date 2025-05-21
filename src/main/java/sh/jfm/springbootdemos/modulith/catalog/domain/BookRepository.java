package sh.jfm.springbootdemos.modulith.catalog.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

/**
 * Repository interface for Book entity operations.
 * Provides CRUD functionality for managing books in the database.
 */
public interface BookRepository extends CrudRepository<Book, UUID> {
}
