package com.kata.ecommerce.cart.domain;

import com.kata.ecommerce.catalog.domain.Product;
import com.kata.ecommerce.common.exception.BusinessException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "cart_items",
        uniqueConstraints = @UniqueConstraint(columnNames = {"cart_id", "product_id"})) // Un produit au plus une fois par panier
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private int quantity;

    CartItem(Cart cart, Product product, int quantity) {
        this.cart = cart;
        this.product = product;
        changeQuantity(quantity);
    }

    void increaseQuantity(int extra) {
        changeQuantity(this.quantity + extra); // Le stock est vérifié sur la quantité cumulée
    }

    void changeQuantity(int newQuantity) {
        if (newQuantity <= 0) {
            throw new BusinessException("La quantité doit être strictement positive");
        }
        if (newQuantity > product.getStockQuantity()) {
            throw new BusinessException("Stock insuffisant pour " + product.getName()
                    + " (demandé : " + newQuantity + ", disponible : " + product.getStockQuantity() + ")");
        }
        this.quantity = newQuantity;
    }

    public BigDecimal getSubTotal() {
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
