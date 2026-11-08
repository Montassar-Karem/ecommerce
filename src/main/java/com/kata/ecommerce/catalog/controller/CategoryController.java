package com.kata.ecommerce.catalog.controller;

import com.kata.ecommerce.catalog.dto.CategoryRequest;
import com.kata.ecommerce.catalog.dto.CategoryResponse;
import com.kata.ecommerce.catalog.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse created = categoryService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    // renvoie l'arbre complet à partir des catégories racines
    @GetMapping
    public List<CategoryResponse> findAllRoots() {
        return categoryService.findAllRoots();
    }

    @GetMapping("/{id}")
    public CategoryResponse findById(@PathVariable Long id) {
        return categoryService.findById(id);
    }

    // parentId permet aussi de déplacer la catégorie, ou de la rendre racine (null)
    @PutMapping("/{id}")
    public CategoryResponse update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return categoryService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // supprimer le lien ne supprime pas le produit
    @PostMapping("/{categoryId}/products/{productId}")
    public CategoryResponse linkProduct(@PathVariable Long categoryId, @PathVariable Long productId) {
        return categoryService.linkProduct(categoryId, productId);
    }

    @DeleteMapping("/{categoryId}/products/{productId}")
    public CategoryResponse unlinkProduct(@PathVariable Long categoryId, @PathVariable Long productId) {
        return categoryService.unlinkProduct(categoryId, productId);
    }
}
