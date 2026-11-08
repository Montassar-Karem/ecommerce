package com.kata.ecommerce;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Bout en bout avec un vrai PostgreSQL : vérifie ce qu'aucun mock ne peut prouver
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class CartIntegrationTest {

    @Autowired MockMvc mockMvc;

    @Test
    void fullCartScenario_withProductDeletion() throws Exception {
        Long phoneId = createProduct("""
                {"name": "Téléphone", "price": 500.00, "stockQuantity": 10}
                """);
        Long cableId = createProduct("""
                {"name": "Câble", "price": 9.99, "stockQuantity": 3}
                """);

        String cartJson = mockMvc.perform(post("/api/carts"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long cartId = JsonPath.parse(cartJson).read("$.id", Long.class);

        mockMvc.perform(post("/api/carts/{id}/items", cartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"items": [{"productId": %d, "quantity": 2}, {"productId": %d, "quantity": 3}]}
                                """.formatted(phoneId, cableId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1029.97));

        // Tout ou rien : le câble dépasserait le stock, donc le téléphone ne doit pas être ajouté non plus
        mockMvc.perform(post("/api/carts/{id}/items", cartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"items": [{"productId": %d, "quantity": 1}, {"productId": %d, "quantity": 1}]}
                                """.formatted(phoneId, cableId)))
                .andExpect(status().isConflict());
        mockMvc.perform(get("/api/carts/{id}/total", cartId))
                .andExpect(jsonPath("$.total").value(1029.97));

        // Sans le ProductDeletedEvent, ce serait une violation de clé étrangère
        mockMvc.perform(delete("/api/products/{id}", phoneId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/carts/{id}", cartId))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].productId").value(cableId))
                .andExpect(jsonPath("$.total").value(29.97));
    }

    private Long createProduct(String json) throws Exception {
        String response = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.parse(response).read("$.id", Long.class);
    }
}
