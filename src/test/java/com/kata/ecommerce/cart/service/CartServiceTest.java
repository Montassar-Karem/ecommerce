package com.kata.ecommerce.cart.service;

import com.kata.ecommerce.cart.domain.Cart;
import com.kata.ecommerce.cart.dto.CartItemRequest;
import com.kata.ecommerce.cart.mapper.CartMapper;
import com.kata.ecommerce.cart.repository.CartRepository;
import com.kata.ecommerce.catalog.domain.Product;
import com.kata.ecommerce.catalog.service.ProductService;
import com.kata.ecommerce.common.exception.BusinessException;
import com.kata.ecommerce.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock CartRepository cartRepository;
    @Mock ProductService productService;
    @Mock CartMapper mapper;
    @InjectMocks CartService cartService;

    @Test
    void addItems_shouldAddSeveralProducts() {
        Cart cart = new Cart();
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(productService.findEntityById(10L)).thenReturn(product(10L, "Téléphone", "500.00", 5));
        when(productService.findEntityById(20L)).thenReturn(product(20L, "Câble", "10.00", 5));

        cartService.addItems(1L, List.of(new CartItemRequest(10L, 1), new CartItemRequest(20L, 2)));

        assertThat(cart.getItems()).hasSize(2);
        assertThat(cart.getTotal()).isEqualByComparingTo("520.00");
    }

    @Test
    void addItems_insufficientStock_shouldThrow() {
        when(cartRepository.findById(1L)).thenReturn(Optional.of(new Cart()));
        when(productService.findEntityById(10L)).thenReturn(product(10L, "Câble", "10.00", 1));

        assertThatThrownBy(() -> cartService.addItems(1L, List.of(new CartItemRequest(10L, 5))))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void getTotal_unknownCart_shouldThrowNotFound() {
        when(cartRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.getTotal(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    private static Product product(Long id, String name, String price, int stock) {
        Product p = new Product(name, new BigDecimal(price), stock);
        p.setId(id);
        return p;
    }
}
