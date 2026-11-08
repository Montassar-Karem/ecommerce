package com.kata.ecommerce.catalog.mapper;

import com.kata.ecommerce.catalog.domain.Category;
import com.kata.ecommerce.catalog.domain.Product;
import com.kata.ecommerce.catalog.dto.CategoryResponse;
import com.kata.ecommerce.catalog.dto.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CatalogMapper {

    ProductResponse toResponse(Product product);

    @Mapping(target = "parentId", source = "parent.id")
    CategoryResponse toResponse(Category category);
}
