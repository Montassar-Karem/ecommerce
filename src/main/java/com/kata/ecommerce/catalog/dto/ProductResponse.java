package com.kata.ecommerce.catalog.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponse(Long id, String name, BigDecimal price, int stockQuantity, List<Long> categoryIds) {}
