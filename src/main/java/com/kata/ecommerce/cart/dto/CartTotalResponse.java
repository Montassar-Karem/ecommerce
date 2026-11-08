package com.kata.ecommerce.cart.dto;

import java.math.BigDecimal;

public record CartTotalResponse(Long cartId, BigDecimal total) {}
