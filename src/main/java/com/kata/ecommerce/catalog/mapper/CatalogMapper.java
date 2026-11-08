package com.kata.ecommerce.catalog.mapper;

import com.kata.ecommerce.catalog.domain.Category;
import com.kata.ecommerce.catalog.domain.Product;
import com.kata.ecommerce.catalog.dto.CategoryResponse;
import com.kata.ecommerce.catalog.dto.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CatalogMapper {

    @Mapping(target = "categoryIds", source = "categories")
    ProductResponse toResponse(Product product);

    // subCategories est mappé en réutilisant cette même méthode : récursion automatique
    @Mapping(target = "parentId", source = "parent.id")
    CategoryResponse toResponse(Category category);

    default List<Long> toIds(Set<Category> categories) {
        return categories.stream().map(Category::getId).sorted().toList();
    }
}
