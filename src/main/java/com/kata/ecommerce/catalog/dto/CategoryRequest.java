package com.kata.ecommerce.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank @Size(max = 255) String name,
        @Size(max = 1000) String description,
        Long parentId // null = racine ; en modification, permet aussi de déplacer ou délier
) {}
