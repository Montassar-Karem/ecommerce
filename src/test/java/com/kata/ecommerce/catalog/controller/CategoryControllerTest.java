package com.kata.ecommerce.catalog.controller;

import com.kata.ecommerce.catalog.dto.CategoryResponse;
import com.kata.ecommerce.catalog.service.CategoryService;
import com.kata.ecommerce.common.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean CategoryService categoryService;

    @Test
    void attachSubCategory_shouldReturnUpdatedParent() throws Exception {
        CategoryResponse child = new CategoryResponse(2L, "Téléphones", null, 1L, List.of(), List.of());
        CategoryResponse parent = new CategoryResponse(1L, "High-Tech", null, null, List.of(child), List.of());
        when(categoryService.attachSubCategory(1L, 2L)).thenReturn(parent);

        mockMvc.perform(post("/api/categories/1/subcategories/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subCategories[0].id").value(2))
                .andExpect(jsonPath("$.subCategories[0].parentId").value(1));
    }

    @Test
    void attachSubCategory_cycle_shouldReturn409() throws Exception {
        when(categoryService.attachSubCategory(3L, 1L))
                .thenThrow(new BusinessException("Impossible : cela créerait un cycle dans l'arbre des catégories"));

        mockMvc.perform(post("/api/categories/3/subcategories/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value(containsString("cycle")));
    }

    @Test
    void linkProduct_shouldReturn200() throws Exception {
        when(categoryService.linkProduct(1L, 5L))
                .thenReturn(new CategoryResponse(1L, "Téléphones", null, null, List.of(), List.of()));

        mockMvc.perform(post("/api/categories/1/products/5"))
                .andExpect(status().isOk());
    }
}
