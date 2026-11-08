package com.kata.ecommerce.cart.domain;

import com.kata.ecommerce.catalog.domain.Product;
import com.kata.ecommerce.common.exception.BusinessException;
import com.kata.ecommerce.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CartTest {

    private Cart cart;
    private Product phone;
    private Product cable;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        phone = product(1L, "Téléphone", "500.00", 10);
        cable = product(2L, "Câble", "9.99", 3);
    }

    @Test
    void emptyCart_totalShouldBeZero() {
        assertThat(cart.getTotal()).isEqualByComparingTo("0");
    }

    @Test
    void addProducts_shouldComputeTotal() {
        cart.addProduct(phone, 2);
        cart.addProduct(cable, 3);

        assertThat(cart.getItems()).hasSize(2);
        assertThat(cart.getTotal()).isEqualByComparingTo("1029.97");
    }

    @Test
    void addSameProductTwice_shouldMergeQuantities() {
        cart.addProduct(phone, 1);
        cart.addProduct(phone, 2);

        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(3);
    }

    @Test
    void addMoreThanStock_shouldThrow() {
        assertThatThrownBy(() -> cart.addProduct(cable, 4))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Stock insuffisant");
    }

    @Test
    void cumulativeAddExceedingStock_shouldThrow() {
        cart.addProduct(cable, 2);

        assertThatThrownBy(() -> cart.addProduct(cable, 2))
                .isInstanceOf(BusinessException.class);
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(2); // État inchangé après l'échec
    }

    @Test
    void addZeroOrNegative_shouldThrow() {
        assertThatThrownBy(() -> cart.addProduct(phone, 0)).isInstanceOf(BusinessException.class);
        assertThatThrownBy(() -> cart.addProduct(phone, -1)).isInstanceOf(BusinessException.class);
    }

    @Test
    void updateQuantity_shouldReplaceQuantity() {
        cart.addProduct(phone, 5);

        cart.updateQuantity(1L, 2);

        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(2);
        assertThat(cart.getTotal()).isEqualByComparingTo("1000.00");
    }

    @Test
    void updateQuantityToZero_shouldRemoveItem() {
        cart.addProduct(phone, 1);

        cart.updateQuantity(1L, 0);

        assertThat(cart.getItems()).isEmpty();
    }

    @Test
    void updateUnknownProduct_shouldThrowNotFound() {
        assertThatThrownBy(() -> cart.updateQuantity(99L, 1))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void removeProduct_shouldRemoveLineAndUpdateTotal() {
        cart.addProduct(phone, 1);
        cart.addProduct(cable, 1);

        cart.removeProduct(1L);

        assertThat(cart.getItems()).extracting(item -> item.getProduct().getName()).containsExactly("Câble");
        assertThat(cart.getTotal()).isEqualByComparingTo("9.99");
    }

    @Test
    void getItems_shouldBeReadOnly() {
        assertThatThrownBy(() -> cart.getItems().clear())
                .isInstanceOf(UnsupportedOperationException.class);
    }

    private static Product product(Long id, String name, String price, int stock) {
        Product p = new Product(name, new BigDecimal(price), stock);
        p.setId(id); // findItem compare les ids, qui viendraient normalement de la base
        return p;
    }
}
