package com.kata.ecommerce.catalog.dto;

import java.util.List;

// DTO récursif : un seul appel renvoie tout l'arbre
public record CategoryResponse(
        Long id,
        String name,
        String description,
        Long parentId, // Seulement l'id pour éviter la boucle parent <-> enfant en JSON
        List<CategoryResponse> subCategories,
        List<ProductResponse> products
) {}
