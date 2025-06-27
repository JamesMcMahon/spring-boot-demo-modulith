package sh.jfm.springbootdemos.modulith.catalog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import sh.jfm.springbootdemos.modulith.http.RestExceptionAdvice;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.json.JsonCompareMode.STRICT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/// Web-layer slice test focused on the successful, happy-path contract.
/// Error scenarios are covered in [CatalogControllerErrorTests] and [RestExceptionAdvice].
@WebMvcTest(CatalogController.class)
class CatalogControllerContractTests {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    Catalog catalog;

    @Test
    void createReturns201WithLocationAndBody() throws Exception {
        var saved = new Book(1L, "1888963093", "Ghost Circles", "Jeff Smith");
        when(catalog.add(new Book(null, saved.isbn(), saved.title(), saved.author())))
                .thenReturn(saved);

        mvc.perform(post("/catalog/books")
                        .contentType("application/json")
                        .content("""
                                {
                                  "isbn": "1888963093",
                                  "title": "Ghost Circles",
                                  "author": "Jeff Smith"
                                }"""))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/catalog/books/1888963093"))
                .andExpect(content().json("""
                        {
                          "isbn": "1888963093",
                          "title": "Ghost Circles",
                          "author": "Jeff Smith"
                        }""", STRICT));
    }

    @Test
    void byIsbnReturns200AndBook() throws Exception {
        when(catalog.byIsbn("1888963123"))
                .thenReturn(Optional.of(
                        new Book(
                                1L,
                                "1888963123",
                                "Treasure Hunters",
                                "Jeff Smith"
                        )));

        mvc.perform(get("/catalog/books/{isbn}", "1888963123"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "isbn": "1888963123",
                          "title": "Treasure Hunters",
                          "author": "Jeff Smith"
                        }""", STRICT));
    }

    @Test
    void updateReturns204OnSuccess() throws Exception {
        doNothing().when(catalog).update(any(Book.class));

        mvc.perform(patch("/catalog/books/{isbn}", "9780439706476")
                        .contentType("application/json")
                        .content("""
                                {
                                  "isbn": "9780439706476",
                                  "title": "Old Man's Cave",
                                  "author": "Jeff Smith"
                                }"""))
                .andExpect(status().isNoContent());
    }
}
