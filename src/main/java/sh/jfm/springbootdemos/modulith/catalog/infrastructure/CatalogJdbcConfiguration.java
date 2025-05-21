package sh.jfm.springbootdemos.modulith.catalog.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.relational.core.mapping.event.BeforeConvertCallback;
import sh.jfm.springbootdemos.modulith.catalog.domain.Book;

import java.util.UUID;

/**
 * Configuration class for Spring Data JDBC related to the catalog module.
 */
@Configuration
@EnableJdbcRepositories(basePackages = "sh.jfm.springbootdemos.modulith.catalog.domain")
public class CatalogJdbcConfiguration {

    /**
     * Ensures that a random UUID is assigned to a new Book if none exists.
     */
    @Bean
    public BeforeConvertCallback<Book> bookBeforeConvertCallback() {
        return (book) -> {
            if (book.getId() == null) {
                return new Book(UUID.randomUUID(), book.getTitle(), book.getAuthor(), book.getCopiesAvailable());
            }
            return book;
        };
    }
}
