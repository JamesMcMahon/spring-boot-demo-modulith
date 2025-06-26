package sh.jfm.springbootdemos.modulith.inventory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.json.JsonCompareMode.STRICT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Test
    void setAvailabilityReturns204() throws Exception {
        when(inventory.setAvailability(42L, false))
                .thenReturn(new Copy(42L, "978-1416928171", "A-1", false));

        mvc.perform(patch("/inventory/copies/{id}", 42L)
                        .content("{\"available\":false}")
                        .contentType("application/json"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteReturns204() throws Exception {
        doNothing().when(inventory).remove(42L);

        mvc.perform(delete("/inventory/copies/{id}", 42L))
                .andExpect(status().isNoContent());
    }
}
