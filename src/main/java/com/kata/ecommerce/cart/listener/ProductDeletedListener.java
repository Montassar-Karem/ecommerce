package com.kata.ecommerce.cart.listener;

import com.kata.ecommerce.cart.repository.CartItemRepository;
import com.kata.ecommerce.catalog.event.ProductDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductDeletedListener {

    private final CartItemRepository cartItemRepository;

    @EventListener
    public void onProductDeleted(ProductDeletedEvent event) {
        cartItemRepository.deleteByProductId(event.productId());
    }
}
