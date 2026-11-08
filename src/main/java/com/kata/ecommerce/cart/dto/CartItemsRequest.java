package com.kata.ecommerce.cart.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CartItemsRequest(@NotEmpty List<@Valid CartItemRequest> items) {}
