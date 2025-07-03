package sh.jfm.springbootdemos.modulith.lending;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LendingExceptionAdviceTests {

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(new FailingController())
                .setControllerAdvice(new LendingExceptionAdvice())
                .build();
    }

    @Test
    void patronNotFoundMapsTo404() throws Exception {
        mvc.perform(get("/patron-missing")).andExpect(status().isNotFound());
    }

    @Test
    void loanNotFoundMapsTo409() throws Exception {
        mvc.perform(get("/loan-missing")).andExpect(status().isConflict());
    }

    @Test
    void noCopyAvailableMapsTo409() throws Exception {
        mvc.perform(get("/no-copy")).andExpect(status().isConflict());
    }

    @RestController
    static class FailingController {
        @GetMapping("/patron-missing")
        void throwPatronMissing() {
            throw new PatronNotFoundException(99L);
        }

        @GetMapping("/loan-missing")
        void throwLoanMissing() {
            throw new LoanNotFoundException(1L, "123");
        }

        @GetMapping("/no-copy")
        void throwNoCopy() {
            throw new NoAvailableCopiesException("123");
        }
    }
}
