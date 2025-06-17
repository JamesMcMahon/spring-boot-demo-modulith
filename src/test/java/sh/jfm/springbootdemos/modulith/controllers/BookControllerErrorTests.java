package sh.jfm.springbootdemos.modulith.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import sh.jfm.springbootdemos.modulith.services.Catalog;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/// Web-layer slice test (`@WebMvcTest`) – focuses on error handling.
/// [Catalog] is mocked; its logic is covered elsewhere.
@WebMvcTest(BookController.class)
class BookControllerErrorTests {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    Catalog catalog;

    @Test
    void byIsbnReturns404WhenCatalogMisses() throws Exception {
        when(catalog.byIsbn("does-not-exist")).thenReturn(Optional.empty());

        mvc.perform(get("/catalog/books/{isbn}", "does-not-exist"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateReturns400OnIsbnMismatch() throws Exception {
        mvc.perform(patch("/catalog/books/{isbn}", "9780596007126")
                        .contentType("application/json")
                        .content("""
                                {
                                  "isbn": "9999999999999",
                                  "title": "Head First Design Patterns",
                                  "author": "Eric Freeman"
                                }"""))
                .andExpect(status().isBadRequest());
    }
}
