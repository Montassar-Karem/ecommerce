package com.kata.ecommerce.cart.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record CartResponse(Long id, Instant createdAt, List<CartItemResponse> items, BigDecimal total) {}
