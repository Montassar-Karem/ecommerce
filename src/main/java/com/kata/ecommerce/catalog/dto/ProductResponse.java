package com.kata.ecommerce.catalog.dto;

import java.math.BigDecimal;

// Pas de liste de catégories : casse le cycle Category <-> Product
public record ProductResponse(Long id, String name, BigDecimal price, int stockQuantity) {}
