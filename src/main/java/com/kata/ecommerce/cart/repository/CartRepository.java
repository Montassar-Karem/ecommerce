package com.kata.ecommerce.cart.repository;

import com.kata.ecommerce.cart.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
