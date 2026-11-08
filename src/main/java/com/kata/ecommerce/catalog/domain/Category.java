package com.kata.ecommerce.catalog.domain;

import com.kata.ecommerce.common.exception.BusinessException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private String name;

    @Setter
    @Column(length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    // REMOVE : supprimer une catégorie supprime sa sous-arborescence.
    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
    private Set<Category> subCategories = new HashSet<>();


    @ManyToMany
    @JoinTable(name = "category_product",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    private Set<Product> products = new HashSet<>();

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void addSubCategory(Category child) {
        if (child == this || child.isAncestorOf(this)) {
            throw new BusinessException("Impossible : cela créerait un cycle dans l'arbre des catégories");
        }
        if (child.parent != null) {
            child.parent.subCategories.remove(child);
        }
        child.parent = this;
        subCategories.add(child);
    }

    public void removeSubCategory(Category child) {
        subCategories.remove(child);
        child.parent = null;
    }


    public void linkProduct(Product product) {
        products.add(product);
        product.getCategories().add(this);
    }

    public void unlinkProduct(Product product) {
        products.remove(product);
        product.getCategories().remove(this);
    }

    private boolean isAncestorOf(Category other) {
        for (Category c = other.parent; c != null; c = c.parent) {
            if (c == this) return true;
        }
        return false;
    }
}
