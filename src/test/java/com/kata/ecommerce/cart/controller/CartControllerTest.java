package com.kata.ecommerce.cart.controller;

import com.kata.ecommerce.cart.dto.CartItemResponse;
import com.kata.ecommerce.cart.dto.CartResponse;
import com.kata.ecommerce.cart.service.CartService;
import com.kata.ecommerce.common.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean CartService cartService;

    @Test
    void create_shouldReturn201() throws Exception {
        when(cartService.create()).thenReturn(new CartResponse(1L, Instant.now(), List.of(), BigDecimal.ZERO));

        mockMvc.perform(post("/api/carts"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/carts/1"))
                .andExpect(jsonPath("$.total").value(0));
    }

    @Test
    void addItems_shouldReturnCartWithTotal() throws Exception {
        CartItemResponse line = new CartItemResponse(10L, "Téléphone", new BigDecimal("500.00"), 2, new BigDecimal("1000.00"));
        when(cartService.addItems(eq(1L), any()))
                .thenReturn(new CartResponse(1L, Instant.now(), List.of(line), new BigDecimal("1000.00")));

        mockMvc.perform(post("/api/carts/1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"items": [{"productId": 10, "quantity": 2}]}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].subTotal").value(1000.00))
                .andExpect(jsonPath("$.total").value(1000.00));
    }

    @Test
    void addItems_emptyList_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/carts/1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"items": []}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addItems_insufficientStock_shouldReturn409() throws Exception {
        when(cartService.addItems(eq(1L), any())).thenThrow(new BusinessException("Stock insuffisant"));

        mockMvc.perform(post("/api/carts/1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"items": [{"productId": 10, "quantity": 99}]}
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void removeSeveralItems_shouldParseProductIds() throws Exception {
        when(cartService.removeItems(eq(1L), any()))
                .thenReturn(new CartResponse(1L, Instant.now(), List.of(), BigDecimal.ZERO));

        mockMvc.perform(delete("/api/carts/1/items").param("productIds", "10,20"))
                .andExpect(status().isOk());

        verify(cartService).removeItems(1L, List.of(10L, 20L));
    }
}
