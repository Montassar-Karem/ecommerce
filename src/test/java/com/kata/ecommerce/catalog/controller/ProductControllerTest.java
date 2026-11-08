package com.kata.ecommerce.catalog.controller;

import com.kata.ecommerce.catalog.dto.ProductResponse;
import com.kata.ecommerce.catalog.service.ProductService;
import com.kata.ecommerce.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean ProductService productService;

    @Test
    void create_valid_shouldReturn201WithLocation() throws Exception {
        when(productService.create(any()))
                .thenReturn(new ProductResponse(1L, "iPhone", new BigDecimal("999.99"), 10, List.of()));

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "iPhone", "price": 999.99, "stockQuantity": 10}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/products/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("iPhone"));
    }

    @Test
    void create_invalid_shouldReturn400WithFieldErrors() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "", "price": -5}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.price").exists())
                .andExpect(jsonPath("$.errors.stockQuantity").exists());
    }

    @Test
    void findById_unknown_shouldReturn404() throws Exception {
        when(productService.findById(99L)).thenThrow(new ResourceNotFoundException("Produit", 99L));

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Produit introuvable avec l'id 99"));
    }
}
