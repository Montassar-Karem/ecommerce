package com.kata.ecommerce.cart.domain;

import com.kata.ecommerce.catalog.domain.Product;
import com.kata.ecommerce.common.exception.BusinessException;
import com.kata.ecommerce.common.exception.ResourceNotFoundException;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

// toute modification des lignes passe par le panier.
@Entity
@Table(name = "carts")
@Getter
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    // retirer une ligne de la liste la supprime en base
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<CartItem> items = new ArrayList<>();

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void addProduct(Product product, int quantity) {
        if (quantity <= 0) {
            throw new BusinessException("La quantité à ajouter doit être strictement positive");
        }
        findItem(product.getId()).ifPresentOrElse(
                item -> item.increaseQuantity(quantity),
                () -> items.add(new CartItem(this, product, quantity))
        );
    }

    // Remplace la quantité ; 0 retire le produit du panier
    public void updateQuantity(Long productId, int quantity) {
        CartItem item = findItem(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit dans le panier", productId));
        if (quantity == 0) {
            items.remove(item);
        } else {
            item.changeQuantity(quantity);
        }
    }

    public void removeProduct(Long productId) {
        boolean removed = items.removeIf(item -> item.getProduct().getId().equals(productId));
        if (!removed) {
            throw new ResourceNotFoundException("Produit dans le panier", productId);
        }
    }

    public BigDecimal getTotal() {
        return items.stream()
                .map(CartItem::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Panier vide = 0, jamais null
    }

    private Optional<CartItem> findItem(Long productId) {
        return items.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
    }
}
