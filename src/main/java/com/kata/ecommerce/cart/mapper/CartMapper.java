package com.kata.ecommerce.cart.mapper;

import com.kata.ecommerce.cart.domain.Cart;
import com.kata.ecommerce.cart.domain.CartItem;
import com.kata.ecommerce.cart.dto.CartItemResponse;
import com.kata.ecommerce.cart.dto.CartResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CartMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "unitPrice", source = "product.price")
    CartItemResponse toResponse(CartItem item);

    CartResponse toResponse(Cart cart);
}
