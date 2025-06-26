package sh.jfm.springbootdemos.modulith.lending;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.json.JsonCompareMode.STRICT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/// Web-layer slice test focused on the successful, happy-path contract.
@WebMvcTest(LendingController.class)
class LendingControllerContractTests {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    Lending lending;

    @Test
    void borrowReturns201WithBody() throws Exception {
        when(lending.borrow(1L, "978-1606994740"))
                .thenReturn(new Loan(
                        99L,
                        42L,
                        "978-1606994740",
                        1L,
                        LocalDate.of(2025, 1, 1)
                ));

        mvc.perform(post("/lending/loans")
                        .contentType("application/json")
                        .content("""
                                {
                                  "patronId": 1,
                                  "isbn": "978-1606994740"
                                }"""))
                .andExpect(status().isCreated())
                .andExpect(content().json("""
                        {
                          "id": 99,
                          "copyId": 42,
                          "isbn": "978-1606994740",
                          "patronId": 1,
                          "dueDate": "2025-01-01"
                        }""", STRICT));
    }

    @Test
    void returnBookReturns204() throws Exception {
        doNothing().when(lending).returnBook(1L, "978-1606994740");

        mvc.perform(post("/lending/returns")
                        .contentType("application/json")
                        .content("""
                                {
                                  "patronId": 1,
                                  "isbn": "978-1606994740"
                                }"""))
                .andExpect(status().isNoContent());
    }

    @Test
    void createPatronReturns201WithLocationAndBody() throws Exception {
        when(lending.addPatron(any(Patron.class))).thenReturn(new Patron(1L));

        mvc.perform(post("/lending/patrons")
                        .contentType("application/json"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/lending/patrons/1"))
                .andExpect(content().json("{\"id\":1}", STRICT));
    }

    @Test
    void loansForPatronReturns200AndList() throws Exception {
        when(lending.findLoansForPatron(1L))
                .thenReturn(List.of(new Loan(
                        99L,
                        42L,
                        "978-1606994740",
                        1L,
                        LocalDate.of(2025, 1, 1)
                )));

        mvc.perform(get("/lending/patrons/{patronId}/loans", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "loans": [
                            {
                              "id": 99,
                              "copyId": 42,
                              "isbn": "978-1606994740",
                              "patronId": 1,
                              "dueDate": "2025-01-01"
                            }
                          ]
                        }""", STRICT));
    }
}
