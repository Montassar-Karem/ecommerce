package com.kata.ecommerce.catalog.domain;

import com.kata.ecommerce.common.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CategoryTest {

    @Test
    void addSubCategory_shouldLinkBothSides() {
        Category parent = new Category("High-Tech", null);
        Category child = new Category("Téléphones", null);

        parent.addSubCategory(child);

        assertThat(parent.getSubCategories()).containsExactly(child);
        assertThat(child.getParent()).isEqualTo(parent);
    }

    @Test
    void addSubCategory_shouldMoveChildFromOldParent() {
        Category oldParent = new Category("Ancien", null);
        Category newParent = new Category("Nouveau", null);
        Category child = new Category("Enfant", null);
        oldParent.addSubCategory(child);

        newParent.addSubCategory(child);

        assertThat(oldParent.getSubCategories()).isEmpty();
        assertThat(newParent.getSubCategories()).contains(child);
    }

    @Test
    void addSubCategory_shouldRejectCycle() {
        Category a = new Category("A", null);
        Category b = new Category("B", null);
        a.addSubCategory(b);

        assertThatThrownBy(() -> b.addSubCategory(a))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("cycle");
    }

    @Test
    void addSubCategory_shouldRejectSelf() {
        Category a = new Category("A", null);

        assertThatThrownBy(() -> a.addSubCategory(a))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void removeSubCategory_shouldMakeChildRoot() {
        Category parent = new Category("Parent", null);
        Category child = new Category("Enfant", null);
        parent.addSubCategory(child);

        parent.removeSubCategory(child);

        assertThat(child.getParent()).isNull();
        assertThat(parent.getSubCategories()).isEmpty();
    }

    @Test
    void linkAndUnlinkProduct_shouldUpdateBothSides() {
        Category category = new Category("Téléphones", null);
        Product product = new Product("iPhone", new BigDecimal("999.99"), 10);

        category.linkProduct(product);
        assertThat(category.getProducts()).contains(product);
        assertThat(product.getCategories()).contains(category);

        category.unlinkProduct(product);
        assertThat(category.getProducts()).isEmpty();
        assertThat(product.getCategories()).isEmpty();
    }
}
