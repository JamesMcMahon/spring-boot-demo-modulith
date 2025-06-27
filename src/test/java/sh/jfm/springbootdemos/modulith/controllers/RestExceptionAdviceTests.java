package sh.jfm.springbootdemos.modulith.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sh.jfm.springbootdemos.modulith.services.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/// Uses standalone MockMvc to assert that [RestExceptionAdvice]
/// maps domain errors to correct HTTP status codes without starting
/// the full Spring context.
class RestExceptionAdviceTests {

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(new FailingController())
                .setControllerAdvice(new RestExceptionAdvice())
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

    @Test
    void copyNotFoundMapsTo404() throws Exception {
        mvc.perform(get("/copy-missing"))
                .andExpect(status().isNotFound());
    }

    @Test
    void noCopiesFreeMapsTo409() throws Exception {
        mvc.perform(get("/no-copies"))
                .andExpect(status().isConflict());
    }

    @Test
    void invalidCopyMapsTo400() throws Exception {
        mvc.perform(get("/invalid-copy"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patronNotFoundMapsTo404() throws Exception {
        mvc.perform(get("/patron-missing"))
                .andExpect(status().isNotFound());
    }

    @Test
    void loanNotFoundMapsTo409() throws Exception {
        mvc.perform(get("/loan-missing"))
                .andExpect(status().isConflict());
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

        @GetMapping("/copy-missing")
        void throwCopyMissing() {
            throw new CopyNotFoundException(42L);
        }

        @GetMapping("/no-copies")
        void throwNoCopies() {
            throw new NoAvailableCopiesException("123");
        }

        @GetMapping("/invalid-copy")
        void throwInvalidCopy() {
            throw new InvalidCopyException("123");
        }

        @GetMapping("/patron-missing")
        void throwPatronMissing() {
            throw new PatronNotFoundException(99L);
        }

        @GetMapping("/loan-missing")
        void throwLoanMissing() {
            throw new LoanNotFoundException(1L, "123");
        }
    }
}
