package com.kata.ecommerce.cart.controller;

import com.kata.ecommerce.cart.dto.CartItemsRequest;
import com.kata.ecommerce.cart.dto.CartResponse;
import com.kata.ecommerce.cart.dto.CartTotalResponse;
import com.kata.ecommerce.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CartResponse> create() {
        CartResponse created = cartService.create();
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{cartId}")
    public CartResponse findById(@PathVariable Long cartId) {
        return cartService.findById(cartId);
    }

    @GetMapping("/{cartId}/total")
    public CartTotalResponse getTotal(@PathVariable Long cartId) {
        return cartService.getTotal(cartId);
    }

    // les quantités se cumulent
    @PostMapping("/{cartId}/items")
    public CartResponse addItems(@PathVariable Long cartId, @Valid @RequestBody CartItemsRequest request) {
        return cartService.addItems(cartId, request.items());
    }

    // PUT remplace les quantités ; quantité 0 = retirer
    @PutMapping("/{cartId}/items")
    public CartResponse updateItems(@PathVariable Long cartId, @Valid @RequestBody CartItemsRequest request) {
        return cartService.updateItems(cartId, request.items());
    }

    @DeleteMapping("/{cartId}/items/{productId}")
    public CartResponse removeItem(@PathVariable Long cartId, @PathVariable Long productId) {
        return cartService.removeItems(cartId, List.of(productId));
    }

    @DeleteMapping("/{cartId}/items")
    public CartResponse removeItems(@PathVariable Long cartId, @RequestParam List<Long> productIds) {
        return cartService.removeItems(cartId, productIds);
    }
}
