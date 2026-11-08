package com.kata.ecommerce.cart.service;

import com.kata.ecommerce.cart.domain.Cart;
import com.kata.ecommerce.cart.dto.CartItemRequest;
import com.kata.ecommerce.cart.dto.CartResponse;
import com.kata.ecommerce.cart.dto.CartTotalResponse;
import com.kata.ecommerce.cart.mapper.CartMapper;
import com.kata.ecommerce.cart.repository.CartRepository;
import com.kata.ecommerce.catalog.service.ProductService;
import com.kata.ecommerce.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final ProductService productService;
    private final CartMapper mapper;

    public CartResponse create() {
        return mapper.toResponse(cartRepository.save(new Cart()));
    }

    @Transactional(readOnly = true)
    public CartResponse findById(Long cartId) {
        return mapper.toResponse(findEntityById(cartId));
    }

    @Transactional(readOnly = true)
    public CartTotalResponse getTotal(Long cartId) {
        Cart cart = findEntityById(cartId);
        return new CartTotalResponse(cart.getId(), cart.getTotal());
    }

    public CartResponse addItems(Long cartId, List<CartItemRequest> items) {
        Cart cart = findEntityById(cartId);
        for (CartItemRequest item : items) {
            cart.addProduct(productService.findEntityById(item.productId()), item.quantity());
        }
        return mapper.toResponse(cart); // Lignes insérées en cascade, pas de save() nécessaire
    }

    public CartResponse updateItems(Long cartId, List<CartItemRequest> items) {
        Cart cart = findEntityById(cartId);
        items.forEach(item -> cart.updateQuantity(item.productId(), item.quantity()));
        return mapper.toResponse(cart);
    }

    public CartResponse removeItems(Long cartId, List<Long> productIds) {
        Cart cart = findEntityById(cartId);
        productIds.forEach(cart::removeProduct);
        return mapper.toResponse(cart);
    }

    private Cart findEntityById(Long cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Panier", cartId));
    }
}
