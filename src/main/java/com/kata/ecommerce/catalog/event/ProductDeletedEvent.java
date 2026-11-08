package com.kata.ecommerce.catalog.event;

// Fait passé, donc immuable : nommé au passé par convention
public record ProductDeletedEvent(Long productId) {}
