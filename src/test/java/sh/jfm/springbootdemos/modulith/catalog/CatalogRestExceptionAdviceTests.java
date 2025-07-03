package sh.jfm.springbootdemos.modulith.catalog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CatalogRestExceptionAdviceTests {

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(new FailingController())
                .setControllerAdvice(new CatalogRestExceptionAdvice())
                .build();
    }

    @Test
    void bookAlreadyExistsMapsTo409() throws Exception {
        mvc.perform(get("/book-exists"))
                .andExpect(status().isConflict());
    }

    @Test
    void bookNotFoundMapsTo404() throws Exception {
        mvc.perform(get("/book-missing"))
                .andExpect(status().isNotFound());
    }

    @RestController
    static class FailingController {

        @GetMapping("/book-exists")
        void throwBookExists() {
            throw new BookAlreadyExistsException("123");
        }

        @GetMapping("/book-missing")
        void throwBookMissing() {
            throw new BookNotFoundException("123");
        }
    }
}
