package sh.jfm.springbootdemos.modulith.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import sh.jfm.springbootdemos.modulith.model.Copy;
import sh.jfm.springbootdemos.modulith.services.Inventory;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.json.JsonCompareMode.STRICT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/// Web-layer slice test focused on the successful, happy-path contract.
@WebMvcTest(InventoryController.class)
class InventoryControllerContractTests {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    Inventory inventory;

    @Test
    void addCopyReturns201WithLocationAndBody() throws Exception {
        when(inventory.add(any()))
                .thenReturn(new Copy(
                        42L,
                        "978-1416928171",
                        "A-1",
                        true
                ));

        mvc.perform(post("/inventory/copies")
                        .contentType("application/json")
                        .content("""
                                {
                                  "isbn": "978-1416928171",
                                  "location": "A-1"
                                }"""))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/inventory/copies/42"))
                .andExpect(content().json("""
                        {
                          "id": 42,
                          "isbn": "978-1416928171",
                          "location": "A-1",
                          "available": true
                        }""", STRICT));
    }

    @Test
    void availabilityReturns200AndCount() throws Exception {
        when(inventory.availability("978-1416928171")).thenReturn(3L);

        mvc.perform(get("/inventory/books/{isbn}/availability", "978-1416928171"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "available": 3
                        }""", STRICT));
    }
}
