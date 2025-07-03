package sh.jfm.springbootdemos.modulith.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InventoryExceptionAdviceTests {

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(new FailingController())
                .setControllerAdvice(new InventoryExceptionAdvice())
                .build();
    }

    @Test
    void invalidCopyMapsTo400() throws Exception {
        mvc.perform(get("/invalid-copy")).andExpect(status().isBadRequest());
    }

    @RestController
    static class FailingController {
        @GetMapping("/invalid-copy")
        void throwInvalidCopy() {
            throw new InvalidCopyException("123");
        }
    }
}
