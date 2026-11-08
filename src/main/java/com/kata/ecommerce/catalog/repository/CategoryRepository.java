package com.kata.ecommerce.catalog.repository;

import com.kata.ecommerce.catalog.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Requête dérivée du nom : WHERE parent_id IS NULL -> les racines de l'arbre
    List<Category> findByParentIsNull();
}
