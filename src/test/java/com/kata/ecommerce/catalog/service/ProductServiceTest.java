package com.kata.ecommerce.catalog.service;

import com.kata.ecommerce.catalog.domain.Category;
import com.kata.ecommerce.catalog.domain.Product;
import com.kata.ecommerce.catalog.dto.ProductRequest;
import com.kata.ecommerce.catalog.mapper.CatalogMapper;
import com.kata.ecommerce.catalog.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock ProductRepository productRepository;
    @Mock CatalogMapper mapper;
    @InjectMocks ProductService productService;

    @Test
    void delete_shouldUnlinkCategoriesBeforeDeleting() {
        Product product = new Product("iPhone", new BigDecimal("999.99"), 10);
        Category phones = new Category("Téléphones", null);
        Category promo = new Category("Promo", null);
        phones.linkProduct(product);
        promo.linkProduct(product);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.delete(1L);

        assertThat(phones.getProducts()).isEmpty();
        assertThat(promo.getProducts()).isEmpty();
        verify(productRepository).delete(product);
    }

    @Test
    void update_shouldModifyFieldsWithoutSave() {
        Product product = new Product("iPhone", new BigDecimal("999.99"), 10);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.update(1L, new ProductRequest("iPhone 16", new BigDecimal("1099.00"), 5));

        assertThat(product.getName()).isEqualTo("iPhone 16");
        assertThat(product.getPrice()).isEqualByComparingTo("1099.00");
        assertThat(product.getStockQuantity()).isEqualTo(5);
        verify(productRepository, never()).save(any());
    }
}
