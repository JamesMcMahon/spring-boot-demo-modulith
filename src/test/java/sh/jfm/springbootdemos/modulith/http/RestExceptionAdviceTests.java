package sh.jfm.springbootdemos.modulith.http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sh.jfm.springbootdemos.modulith.catalog.BookAlreadyExistsException;
import sh.jfm.springbootdemos.modulith.catalog.BookNotFoundException;
import sh.jfm.springbootdemos.modulith.inventory.CopyNotFoundException;
import sh.jfm.springbootdemos.modulith.inventory.InvalidCopyException;
import sh.jfm.springbootdemos.modulith.inventory.NoAvailableCopiesException;
import sh.jfm.springbootdemos.modulith.lending.LoanNotFoundException;
import sh.jfm.springbootdemos.modulith.lending.PatronNotFoundException;

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
                .standaloneSetup(new FailingController())      // minimal controller
                .setControllerAdvice(new RestExceptionAdvice()) // advice under test
                .build();
    }

    @Test
    void bookAlreadyExistsMapsTo409() throws Exception {
        mvc.perform(get("/exists"))
                .andExpect(status().isConflict());                  // 409
    }

    @Test
    void bookNotFoundMapsTo404() throws Exception {
        mvc.perform(get("/missing"))
                .andExpect(status().isNotFound());                  // 404
    }

    @Test
    void copyNotFoundMapsTo404() throws Exception {
        mvc.perform(get("/copy-missing"))
                .andExpect(status().isNotFound());
    }

    @Test
    void patronNotFoundMapsTo404() throws Exception {
        mvc.perform(get("/patron-missing"))
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
    void loanNotFoundMapsTo409() throws Exception {
        mvc.perform(get("/loan-missing"))
                .andExpect(status().isConflict());
    }

    @RestController
    static class FailingController {

        @GetMapping("/exists")
        void throwExists() {
            throw new BookAlreadyExistsException("123");
        }

        @GetMapping("/missing")
        void throwMissing() {
            throw new BookNotFoundException("123");
        }

        @GetMapping("/copy-missing")
        void throwCopyMissing() {
            throw new CopyNotFoundException(42L);
        }

        @GetMapping("/patron-missing")
        void throwPatronMissing() {
            throw new PatronNotFoundException(99L);
        }

        @GetMapping("/no-copies")
        void throwNoCopies() {
            throw new NoAvailableCopiesException("123");
        }

        @GetMapping("/invalid-copy")
        void throwInvalidCopy() {
            throw new InvalidCopyException("123");
        }

        @GetMapping("/loan-missing")
        void throwLoanMissing() {
            throw new LoanNotFoundException(1L, "123");
        }
    }
}
