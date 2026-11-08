package com.kata.ecommerce.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Sans parentId : renommer ne peut plus détacher la catégorie.es liens passent par /subcategories
public record CategoryUpdateRequest(
        @NotBlank @Size(max = 255) String name,
        @Size(max = 1000) String description
) {}
