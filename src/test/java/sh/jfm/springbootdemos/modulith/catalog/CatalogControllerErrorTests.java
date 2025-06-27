package sh.jfm.springbootdemos.modulith.catalog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/// Web-layer slice that verifies **error paths** for [CatalogController].
///
/// **This test is for error logic implemented specifically in the controller
/// layer that is untested by domain tests.**
///
/// All domain behavior is mocked – we only assert HTTP contract for failures.
@WebMvcTest(CatalogController.class)
class CatalogControllerErrorTests {

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
        mvc.perform(patch("/catalog/books/{isbn}", "initial-isbn")
                        .contentType("application/json")
                        .content("""
                                {
                                  "isbn": "mismatched-isbn",
                                  "title": "Crown of Horns",
                                  "author": "Jeff Smith"
                                }"""))
                .andExpect(status().isBadRequest());
    }
}
