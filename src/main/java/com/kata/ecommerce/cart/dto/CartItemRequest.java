package com.kata.ecommerce.cart.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CartItemRequest(
        @NotNull Long productId,
        @NotNull @PositiveOrZero Integer quantity
) {}
