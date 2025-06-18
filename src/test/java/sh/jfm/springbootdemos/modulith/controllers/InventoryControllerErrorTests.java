package sh.jfm.springbootdemos.modulith.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import sh.jfm.springbootdemos.modulith.services.BookNotFoundException;
import sh.jfm.springbootdemos.modulith.services.CopyNotFoundException;
import sh.jfm.springbootdemos.modulith.services.Inventory;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/// Web-layer slice test for Inventory error handling
@WebMvcTest(InventoryController.class)
class InventoryControllerErrorTests {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    Inventory inventory;

    @Test
    void postReturns404WhenBookNotFound() throws Exception {
        when(inventory.add(org.mockito.ArgumentMatchers.any()))
                .thenThrow(new BookNotFoundException("unknown-isbn"));

        mvc.perform(post("/inventory/copies")
                        .contentType("application/json")
                        .content("""
                                {
                                  "id": null,
                                  "isbn": "unknown-isbn",
                                  "location": "Main Library"
                                }"""))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteReturns404WhenCopyNotFound() throws Exception {
        long missingId = 12345L;
        org.mockito.Mockito.doThrow(new CopyNotFoundException(missingId))
                .when(inventory).remove(missingId);

        mvc.perform(delete("/inventory/copies/{id}", missingId))
                .andExpect(status().isNotFound());
    }
}
