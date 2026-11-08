package com.kata.ecommerce.catalog.service;

import com.kata.ecommerce.catalog.domain.Category;
import com.kata.ecommerce.catalog.domain.Product;
import com.kata.ecommerce.catalog.dto.CategoryRequest;
import com.kata.ecommerce.catalog.mapper.CatalogMapper;
import com.kata.ecommerce.catalog.repository.CategoryRepository;
import com.kata.ecommerce.common.exception.ResourceNotFoundException;
import com.kata.ecommerce.catalog.dto.CategoryUpdateRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock CategoryRepository categoryRepository;
    @Mock ProductService productService;
    @Mock CatalogMapper mapper;

    @InjectMocks CategoryService categoryService;

    @Test
    void create_withParent_shouldAttachToParent() {
        Category parent = new Category("High-Tech", null);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(categoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        categoryService.create(new CategoryRequest("Téléphones", null, 1L));

        assertThat(parent.getSubCategories())
                .extracting(Category::getName)
                .containsExactly("Téléphones");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void findById_unknown_shouldThrowNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void linkProduct_shouldLinkProductToCategory() {
        Category category = new Category("Téléphones", null);
        Product product = new Product("iPhone", new BigDecimal("999.99"), 10);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productService.findEntityById(5L)).thenReturn(product);

        categoryService.linkProduct(1L, 5L);

        assertThat(category.getProducts()).contains(product);
        verify(categoryRepository, never()).save(any()); // Dirty checking : pas de save() explicite
    }

    @Test
    void attachSubCategory_shouldMoveChildUnderParent() {
        Category parent = new Category("High-Tech", null);
        Category child = new Category("Téléphones", null);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(child));

        categoryService.attachSubCategory(1L, 2L);

        assertThat(child.getParent()).isEqualTo(parent);
    }

    // Non-régression : renommer ne doit plus détacher la catégorie de son parent
    @Test
    void update_shouldNotChangeParent() {
        Category parent = new Category("High-Tech", null);
        Category child = new Category("Téléphones", null);
        parent.addSubCategory(child);
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(child));

        categoryService.update(2L, new CategoryUpdateRequest("Smartphones", null));

        assertThat(child.getName()).isEqualTo("Smartphones");
        assertThat(child.getParent()).isEqualTo(parent);
    }
}
